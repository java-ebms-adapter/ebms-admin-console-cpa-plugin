package nl.clockwork.ebms.admin.plugin.cpa.model;

import java.io.Serializable;

public class Url implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String transportId;
	private String url;

	public Url(String transportId, String url)
	{
		this.transportId = transportId;
		this.url = url;
	}
	public String getTransportId()
	{
		return transportId;
	}
	public void setTransportId(String transportId)
	{
		this.transportId = transportId;
	}
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
}
