// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
//
// $Id: Sensor.java,v 1.3 2001/06/02 08:59:59 dustin Exp $

package net.spy.temperature;

import net.spy.db.SpyDB;
import java.sql.*;
import java.util.*;

public class Sensor extends Object implements java.io.Serializable {

	private int sensor_id=-1;
	private String serial=null;
	private String name=null;
	private boolean active=true;
	private int low=0;
	private int high=0;

	/**
	 * Get a new Sensor object with the given sensor_id.
	 *
	 * @exception Exception on stuff like invalid or uknown ids or other
	 * failures.
	 */
	public Sensor(int sensor_id) throws Exception {
		super();
		getSensor(sensor_id);
	}

	private Sensor(ResultSet rs) throws Exception {
		super();
		initFromResultSet(rs);
	}

	/**
	 * Get the sensor ID.
	 */
	public int getSensorID() {
		return(sensor_id);
	}

	/**
	 * Get the sensor's serial number.
	 */
	public String getSerial() {
		return(serial);
	}

	/**
	 * Get the sensor's descriptive name.
	 */
	public String getName() {
		return(name);
	}

	/**
	 * Is this sensor active?
	 */
	public boolean isActive() {
		return(active);
	}

	/**
	 * What's the low threshold for this sensor?
	 */
	public int getLow() {
		return(low);
	}

	/**
	 * What's the high threshold for this sensor?
	 */
	public int getHigh() {
		return(high);
	}

	/**
	 * Get an Enumeration of Sensor objects representing all known sensors.
	 *
	 * @exception Exception when DB problems arrise
	 */
	public static Collection getSensors() throws Exception {
		List l=new ArrayList(8);
		SpyDB db=new SpyDB(new TempConf());
		ResultSet rs=db.executeQuery("select * from sensors order by name");

		while(rs.next()) {
			l.add(new Sensor(rs));
		}
		rs.close();
		db.close();

		return(l);
	}

	/**
	 * Printable representation of this object.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append(sensor_id);
		sb.append("\t");
		sb.append(serial);
		sb.append(":  ");
		sb.append(name);
		if(!active) {
			sb.append(" (not active)");
		}

		return(sb.toString());
	}

	private void getSensor(int sensor_id) throws Exception {
		SpyDB db=new SpyDB(new TempConf());
		PreparedStatement pst=db.prepareStatement(
			"select * from sensors where sensor_id = ? "
			);
		pst.setInt(1, sensor_id);
		ResultSet rs=pst.executeQuery();
		rs.next();
		initFromResultSet(rs);
		db.close();
	}

	private void initFromResultSet(ResultSet rs) throws Exception {
		sensor_id = rs.getInt("sensor_id");
		serial = rs.getString("serial");
		name = rs.getString("name");
		active=rs.getBoolean("active");
		low=rs.getInt("low");
		high=rs.getInt("high");
	}

	public static void main(String args[]) throws Exception {
		if(args.length>0) {
			int id=Integer.parseInt(args[0]);
			Sensor s=new Sensor(id);
			System.out.println("Found this:  " + s);
		} else {
			for(Iterator i=Sensor.getSensors().iterator(); i.hasNext(); ) {
				System.out.println(i.next());
			}
		}
	}
}
