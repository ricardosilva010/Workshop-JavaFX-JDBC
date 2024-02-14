package org.example.course.model.services;

import org.example.course.model.dao.DaoFactory;
import org.example.course.model.dao.SellerDao;
import org.example.course.model.entities.Seller;

import java.util.List;

public class SellerService
{
    private SellerDao sellerDao = DaoFactory.createSellerDao();

    public List<Seller> findAll()
    {
        return sellerDao.findAll();
    }

    public void saveOrUpdate(Seller seller)
    {
        if (seller.getId() == null)
        {
            sellerDao.insert(seller);
        }
        else
        {
            sellerDao.update(seller);
        }
    }

    public void remove(Seller seller)
    {
        sellerDao.deleteById(seller.getId());
    }
}
