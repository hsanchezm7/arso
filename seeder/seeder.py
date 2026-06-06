import os
import time
import sys
import json
import requests
import random

API_URL = 'http://pasarela:8080'

def cargar_usuarios():
    with open('usuarios.json', 'r', encoding='utf-8') as f:
        return json.load(f)

def cargar_productos():
    with open('productos.json', 'r', encoding='utf-8') as f:
        return json.load(f)

def esperar_servicios():
    print("⏳ Esperando a que la pasarela (API Gateway) esté lista...", flush=True)
    while True:
        try:
            requests.get(API_URL, timeout=5)
            print("✅ Pasarela accesible.", flush=True)
            break
        except requests.exceptions.RequestException:
            time.sleep(3)
            
    print("⏳ Esperando a que el microservicio de Usuarios esté listo...", flush=True)
    while True:
        try:
            res = requests.get(f"{API_URL}/usuarios/buscar?email=esperar@test.com", timeout=5)
            if res.status_code not in [500, 502, 503, 504]:
                print("✅ Microservicio de Usuarios accesible.", flush=True)
                break
        except requests.exceptions.RequestException:
            pass
        time.sleep(3)
        
    print("⏳ Esperando a que el microservicio de Productos esté listo...", flush=True)
    while True:
        try:
            res = requests.get(f"{API_URL}/productos/categorias", timeout=5)
            if res.status_code == 200:
                print("✅ Microservicio de Productos accesible.", flush=True)
                break
        except requests.exceptions.RequestException:
            pass
        time.sleep(3)

    print("⏳ Esperando a que el microservicio de Compraventas esté listo...", flush=True)
    while True:
        try:
            res = requests.get(f"{API_URL}/compraventas", timeout=5)
            if res.status_code not in [500, 502, 503, 504]:
                print("✅ Microservicio de Compraventas accesible. Iniciando script de sembrado...", flush=True)
                break
        except requests.exceptions.RequestException:
            pass
        time.sleep(3)

def intentar_registrar_usuario(usuario):
    response = requests.post(f"{API_URL}/usuarios", json=usuario)
    if response.status_code in [200, 201]:
        print(f"✅ Usuario seeder creado: {usuario['email']}", flush=True)
        return True
    else:
        return False

def login(usuario):
    response = requests.post(f"{API_URL}/auth/login", json={
        "username": usuario["email"],
        "password": usuario["clave"]
    })
    response.raise_for_status()
    return response.json()["accessToken"]

def iniciar():
    try:
        esperar_servicios()
        
        usuarios = cargar_usuarios()
        productos_mock = cargar_productos()
        
        if not usuarios:
            print("El archivo usuarios.json está vacío.", flush=True)
            sys.exit(1)
            
        admin_usuario = usuarios[0]
        es_primera_vez = intentar_registrar_usuario(admin_usuario)
        
        if not es_primera_vez:
            print("Usuarios detectados en la base de datos. Cancelando seeding...", flush=True)
            sys.exit(0)
            
        # Si era la primera vez, registramos al resto de usuarios
        for u in usuarios[1:]:
            intentar_registrar_usuario(u)
            
        print("Obteniendo tokens de acceso para cada usuario...", flush=True)
        tokens_usuarios = []
        for u in usuarios:
            token = login(u)
            tokens_usuarios.append({"usuario": u, "token": token})

        print(f"Comenzando el reparto de {len(productos_mock)} productos...", flush=True)
        
        # Para resultados reproducibles, se puede usar random.seed(42)
        # random.seed(42)
        
        producto_idx = 0
        total_productos = len(productos_mock)
        productos_creados = []
        
        while producto_idx < total_productos:
            for item in tokens_usuarios:
                if producto_idx >= total_productos:
                    break
                    
                cantidad = random.randint(1, 3)
                productos_asignados = productos_mock[producto_idx:producto_idx + cantidad]
                producto_idx += cantidad
                
                headers = {"Authorization": f"Bearer {item['token']}"}
                for p in productos_asignados:
                    res = requests.post(f"{API_URL}/productos", json=p, headers=headers)
                    if res.status_code in [200, 201]:
                        print(f"📦 '{p['titulo']}' subido por {item['usuario']['email']}", flush=True)
                        location = res.headers.get("Location")
                        if location:
                            prod_id = location.rstrip('/').split('/')[-1]
                            productos_creados.append({
                                "idProducto": prod_id,
                                "owner_email": item['usuario']['email'],
                                "titulo": p['titulo']
                            })
                    else:
                        print(f"❌ Error al crear '{p['titulo']}': {res.text}", flush=True)

        print(f"Generando compraventas aleatorias...", flush=True)
        random.shuffle(productos_creados)
        num_ventas = max(1, len(productos_creados) // 3)
        productos_a_vender = productos_creados[:num_ventas]
        
        for prod in productos_a_vender:
            posibles_compradores = [t for t in tokens_usuarios if t['usuario']['email'] != prod['owner_email']]
            if not posibles_compradores:
                continue
                
            comprador = random.choice(posibles_compradores)
            headers = {"Authorization": f"Bearer {comprador['token']}"}
            payload = {"idProducto": prod['idProducto']}
            
            res = requests.post(f"{API_URL}/compraventas", json=payload, headers=headers)
            if res.status_code in [200, 201]:
                print(f"🤝 Compraventa completada: '{prod['titulo']}' comprado por {comprador['usuario']['email']}", flush=True)
                compraventa_location = res.headers.get("Location")
                if compraventa_location:
                    id_compraventa = compraventa_location.rstrip('/').split('/')[-1]
                    
                    val_comprador = {
                        "idCompraventa": id_compraventa,
                        "rolEvaluador": "comprador",
                        "puntuacion": random.randint(3, 5),
                        "comentario": "Transacción rápida y sin problemas. Recomendado."
                    }
                    requests.post(f"{API_URL}/valoraciones", json=val_comprador)
                    
                    val_vendedor = {
                        "idCompraventa": id_compraventa,
                        "rolEvaluador": "vendedor",
                        "puntuacion": random.randint(4, 5),
                        "comentario": "Muy buen trato. Comprador serio."
                    }
                    requests.post(f"{API_URL}/valoraciones", json=val_vendedor)
                    print(f"   -> ⭐ Valoraciones cruzadas emitidas", flush=True)
            else:
                print(f"❌ Error al comprar '{prod['titulo']}': {res.text}", flush=True)

        print(f"Generando visualizaciones aleatorias...", flush=True)
        for prod in productos_creados:
            num_views = random.randint(2, 70)
            for _ in range(num_views):
                requests.post(f"{API_URL}/productos/{prod['idProducto']}/visualizaciones")
            print(f"👀 {num_views} visualizaciones añadidas a '{prod['titulo']}'", flush=True)


        print("Sembrado completo finalizado correctamente", flush=True)
        sys.exit(0)
    except Exception as e:
        print(f"Error durante el sembrado: {str(e)}", flush=True)
        sys.exit(1)

if __name__ == "__main__":
    iniciar()
