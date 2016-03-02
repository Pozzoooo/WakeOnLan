package com.pozzo.wakeonlan.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * The infos we need to wake a machine.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class WakeEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private String macAddress;
	private String ip;
	private int port;
	private String triggerSsid;
	private Date deletedDate;
	private Date lastWolSentDate;
	private int wolCount;
	private int timeRangeIndex;
	private long startLimit;
	private long endLimit;

	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setTriggerSsid(String triggerSsid) {
		this.triggerSsid = triggerSsid;
	}
	public String getTriggerSsid() {
		return triggerSsid;
	}
	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}
	public Date getDeletedDate() {
		return deletedDate;
	}
	public void setLastWolSentDate(Date lastWolSentDate) {
		this.lastWolSentDate = lastWolSentDate;
	}
	public Date getLastWolSentDate() {
		return lastWolSentDate;
	}
	public void setWolCount(int wolCount) {
		this.wolCount = wolCount;
	}
	public int getWolCount() {
		return wolCount;
	}
	/**
	 * @return new value.
	 */
	public int increasCount() {
		return ++wolCount;
	}

	public long getStartLimit() {
		return startLimit;
	}

	public void setStartLimit(long startLimit) {
		this.startLimit = startLimit;
	}

	public long getEndLimit() {
		return endLimit;
	}

	public void setEndLimit(long endLimit) {
		this.endLimit = endLimit;
	}

	public int getTimeRangeIndex() {
		return timeRangeIndex;
	}

	public void setTimeRangeIndex(int timeRangeIndex) {
		this.timeRangeIndex = timeRangeIndex;
	}
}
