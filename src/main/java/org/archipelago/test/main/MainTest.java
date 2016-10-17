package org.archipelago.test.main;

import org.archipelago.test.domain.ClassOne;
import org.archipelago.test.domain.ClassTwo;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

/**
 * Created by GJULESGB on 19/08/2016.
 */
public class MainTest {

	public static void main(String[] args) {
		OObjectDatabaseTx db = new OObjectDatabaseTx("plocal:C:\\Users\\ABM589\\Desktop\\Output\\OrientDb\\tmp")
				.open();
		db.getEntityManager().registerEntityClass(ClassOne.class);
		db.getEntityManager().registerEntityClass(ClassTwo.class);

		ClassTwo p = db.newInstance(ClassTwo.class);
		p.setId(200l);  
		
		db.save( p );
		db.close();
	}

}
