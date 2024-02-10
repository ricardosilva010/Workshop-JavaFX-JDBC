package org.example.course.db;

public class DbIntegrityException extends RuntimeException
{
    public DbIntegrityException(String msg)
    {
        super(msg);
    }
}
