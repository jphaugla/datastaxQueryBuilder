package com.dse.demo.app;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.dse.driver.api.querybuilder.DseQueryBuilder;
import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.querybuilder.BuildableQuery;
import com.datastax.oss.driver.api.querybuilder.select.Select;

import java.net.InetSocketAddress;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom;

public class QBTest {

	public static void main(String[] args) {
//  put node name, username, and password here
		String node = "jphmac";
		String username = "cassandra";
		String upass = "cassandra";
		String dcname = "dc1";

		CqlSession session;

		// Connect to the cluster and keyspace "authtest"
		session = DseSession.builder().addContactPoint(new InetSocketAddress(node, 9042)).withAuthCredentials(username,upass).withLocalDatacenter(dcname).build();


		// Create keyspace and table
		session.execute("CREATE KEYSPACE IF NOT EXISTS authtest WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};");
		session.execute("CREATE TABLE IF NOT EXISTS authtest.users (email text PRIMARY KEY, age int, city text, firstname text, lastname text);");
		//  this is not query builder-this is a simple insert
		// Insert one record into the users table
		session.execute("INSERT INTO authtest.users (lastname, age, city, email, firstname) VALUES ('Jones', 35, 'Austin', 'bob@example.com', 'Bob')");

		// Simple Select from table
		ResultSet results = session.execute("SELECT * FROM authtest.users");
		for (Row row : results) {
			System.out.println("simple session execute");
			System.out.format("%s %s %s %s %d\n", row.getString("firstname"), row.getString("lastname"), row.getString("city"), row.getString("email"), row.getInt("age"));
		}
		//  Build Query example

		Select query = selectFrom("authtest", "users").column("email"); // SELECT email FROM authtest.users
		SimpleStatement statement = query.build();

		ResultSet rs = session.execute(statement);
		Row row = rs.one();
		System.out.println("Query Builder");
		System.out.println(row.getString("email"));

		// During application initialization with bindmarker

		// Select selectUser = selectFrom("authtest","users").column("firstname").column("lastname").column("email").whereColumn("email").isEqualTo(bindMarker());
		Select selectUser = selectFrom("authtest","users").all().whereColumn("email").isEqualTo(bindMarker());

		// SELECT * FROM user WHERE id=?
		PreparedStatement preparedSelectUser = session.prepare(selectUser.build());

		// At runtime:
		System.out.println("bound query builder");
		ResultSet rs1 = session.execute(preparedSelectUser.bind("bob@example.com"));
		Row row1 = rs1.one();
		System.out.println(row1.getString("email") + " " + row1.getString("firstname") + " " + row1.getString("lastname"));
			// INSERT INTO authtest.users (email,firstname,lastname,city,age) VALUES (:email,:firstname,:lastname,:city,:age) USING TTL :ttl

		BuildableQuery query1 = DseQueryBuilder.insertInto("authtest", "users")
				.value("email", DseQueryBuilder.bindMarker("email"))
				.value("firstname", DseQueryBuilder.bindMarker("firstname"))
				.value("lastname", DseQueryBuilder.bindMarker("lastname"))
				.value("city", DseQueryBuilder.bindMarker("city"))
				.value("age",DseQueryBuilder.bindMarker("age"))
				.usingTtl(DseQueryBuilder.bindMarker("ttl"));
		PreparedStatement preparedStatement = session.prepare(query1.asCql());
		BoundStatement boundStatement = preparedStatement.boundStatementBuilder()
				.setString("email", "jasonhhh@gmail.com")
				.setString("firstname", "Jason")
				.setString("lastname", "Holison")
				.setString("city", "Minneapolis")
				.setInt("age",45)
				.setConsistencyLevel(ConsistencyLevel.ALL)
				.setInt("ttl", 3600)
				.build();
		session.execute(boundStatement);

		//  final count all
		Select selectCountAll =
				DseQueryBuilder.selectFrom("authtest", "users")
						.countAll()
						.whereColumn("email")
						.isEqualTo(DseQueryBuilder.bindMarker("email"));
		PreparedStatement preparedCountAll = session.prepare(selectCountAll.build());

		ResultSet rs3 = session.execute(preparedCountAll.bind("bob@example.com"));
		System.out.println("Count All");
		Row row3 = rs3.one();
		System.out.println(row3.getLong(0));

	}
}
