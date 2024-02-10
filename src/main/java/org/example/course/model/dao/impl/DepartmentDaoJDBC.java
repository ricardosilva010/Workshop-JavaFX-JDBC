package org.example.course.model.dao.impl;

import org.example.course.db.DB;
import org.example.course.db.DbException;
import org.example.course.db.DbIntegrityException;
import org.example.course.model.dao.DepartmentDao;
import org.example.course.model.entities.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJDBC implements DepartmentDao
{
	private Connection connection;

	public DepartmentDaoJDBC(Connection connection)
	{
		this.connection = connection;
	}

	@Override
	public void insert(Department department)
	{
		PreparedStatement preparedStatement = null;

		try
		{
			preparedStatement = connection.prepareStatement("INSERT INTO department (Name) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS);

			preparedStatement.setString(1, department.getName());

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0)
			{
				ResultSet resultSet = preparedStatement.getGeneratedKeys();
				if (resultSet.next())
				{
					int id = resultSet.getInt(1);
					department.setId(id);
				}
				DB.closeResultSet(resultSet);
			}
			else
			{
				throw new DbException("Unexpected error! No rows affected!");
			}
		}
		catch (SQLException e)
		{
			throw new DbException(e.getMessage());
		}
		finally
		{
			DB.closeStatement(preparedStatement);
		}
	}

	@Override
	public void update(Department department)
	{
		PreparedStatement preparedStatement = null;

		try
		{
			preparedStatement = connection.prepareStatement("UPDATE department SET Name = ? WHERE Id = ?");

			preparedStatement.setString(1, department.getName());
			preparedStatement.setInt(2, department.getId());

			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DbException(e.getMessage());
		}
		finally
		{
			DB.closeStatement(preparedStatement);
		}
	}

	@Override
	public void deleteById(Integer id)
	{
		PreparedStatement preparedStatement = null;

		try
		{
			preparedStatement = connection.prepareStatement("DELETE FROM department WHERE Id = ?");

			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DbIntegrityException(e.getMessage());
		}
		finally
		{
			DB.closeStatement(preparedStatement);
		}
	}

	@Override
	public Department findById(Integer id)
	{
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try
		{
			preparedStatement = connection.prepareStatement("SELECT * FROM department WHERE Id = ?");

			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next())
			{
				return instantiateDepartment(resultSet);
			}
			return null;
		}
		catch (SQLException e)
		{
			throw new DbException(e.getMessage());
		}
		finally
		{
			DB.closeResultSet(resultSet);
			DB.closeStatement(preparedStatement);
		}
	}

	@Override
	public List<Department> findAll()
	{
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try
		{
			preparedStatement = connection.prepareStatement("SELECT * FROM department ORDER BY Name");

			resultSet = preparedStatement.executeQuery();

			List<Department> departmentList = new ArrayList<>();

			while (resultSet.next())
			{
				departmentList.add(instantiateDepartment(resultSet));
			}
			return departmentList;
		}
		catch (SQLException e)
		{
			throw new DbException(e.getMessage());
		}
		finally
		{
			DB.closeResultSet(resultSet);
			DB.closeStatement(preparedStatement);
		}
	}

	private Department instantiateDepartment(ResultSet resultSet) throws SQLException
	{
		Department department = new Department();
		department.setId(resultSet.getInt("Id"));
		department.setName(resultSet.getString("Name"));

		return department;
	}

}
