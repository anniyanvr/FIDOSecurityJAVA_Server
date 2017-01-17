package org.psl.fidouaf.res.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public final class ConnectionUtil {
	private ConnectionUtil() {

	}

	public static Session OpenSession() {
		// Configuration configuration = new Configuration();
		// configuration.configure();
		// ServiceRegistry serviceRegistry = new
		// StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
		// SessionFactory sessionFactory = new
		// Configuration().configure().buildSessionFactory(serviceRegistry);
		// Session session = sessionFactory.openSession();
		// return session;

		// creating configuration object
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.cfg.xml");// populates the data of
														// the configuration
														// file

		// creating seession factory object
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties()).build();
		SessionFactory sessionFactory = new Configuration().configure()
				.buildSessionFactory(serviceRegistry);

		// creating session object
		Session session = sessionFactory.openSession();
		return session;
	}
}
