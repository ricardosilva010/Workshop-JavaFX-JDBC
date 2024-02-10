package org.example.course.model.dao;

import org.example.course.db.DB;
import org.example.course.model.dao.impl.DepartmentDaoJDBC;
import org.example.course.model.dao.impl.SellerDaoJDBC;

public class DaoFactory
{
	public static SellerDao createSellerDao()
	{
		return new SellerDaoJDBC(DB.getConnection());
	}
	
	public static DepartmentDao createDepartmentDao()
	{
		return new DepartmentDaoJDBC(DB.getConnection());
	}
}
