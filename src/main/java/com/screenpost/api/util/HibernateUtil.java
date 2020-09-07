package com.screenpost.api.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			try {
				// Create the SessionFactory from standard (hibernate.cfg.xml)
				// config file.
				Configuration configuration = new Configuration().configure();
				ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
						.applySettings(configuration.getProperties()).build();
				sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			} catch (Throwable ex) {
				throw new ExceptionInInitializerError(ex);
			}
		}
		return sessionFactory;
	}

}
