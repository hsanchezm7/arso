using Microsoft.EntityFrameworkCore;

using ValoracionesApi.Models;

namespace ValoracionesApi.Repositories;

public class RepositorioValoracionesEFCore : IRepositorio<Valoracion, int>
{
    protected readonly ValoracionesDbContext _context;
    protected readonly DbSet<Valoracion> _dbSet;

    public RepositorioValoracionesEFCore(ValoracionesDbContext context)
    {
        _context = context;
        _dbSet = context.Set<Valoracion>();
    }

    public async Task<int> AddAsync(Valoracion entity)
    {
        await _dbSet.AddAsync(entity);
        await _context.SaveChangesAsync();

        return entity.Id;
    }

    public async Task DeleteAsync(Valoracion entity)
    {
        _dbSet.Remove(entity);
        await _context.SaveChangesAsync();
    }

    public Task<List<Valoracion>> GetAllAsync()
    {
        return _dbSet.ToListAsync();
    }

    public async Task<Valoracion?> GetByIdAsync(int id)
    {
        return await _dbSet.AsNoTracking().FirstOrDefaultAsync(e => e.Id == id);
    }

    public async Task<List<int>> GetIdsAsync()
    {
        return await _dbSet.Select(e => e.Id).ToListAsync();
    }

    public async Task UpdateAsync(Valoracion entity)
    {
        _dbSet.Update(entity);
        await _context.SaveChangesAsync();
    }
}
