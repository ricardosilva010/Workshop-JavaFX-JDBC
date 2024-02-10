package org.example.course.model.services;

import org.example.course.model.dao.DaoFactory;
import org.example.course.model.dao.DepartmentDao;
import org.example.course.model.entities.Department;

import java.util.List;

public class DepartmentService
{
    private DepartmentDao departmentDao = DaoFactory.createDepartmentDao();

    public List<Department> findAll()
    {
        return departmentDao.findAll();
    }
}
