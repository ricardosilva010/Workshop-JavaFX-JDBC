package org.example.course.model.dao;

import org.example.course.model.entities.Seller;
import org.example.course.model.entities.Department;

import java.util.List;

public interface SellerDao {

	void insert(Seller obj);
	void update(Seller obj);
	void deleteById(Integer id);
	Seller findById(Integer id);
	List<Seller> findAll();
	List<Seller> findByDepartment(Department department);
}
