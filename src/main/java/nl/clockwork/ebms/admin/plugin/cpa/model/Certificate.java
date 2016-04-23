package nl.clockwork.ebms.admin.plugin.cpa.model;

import java.util.List;

import org.apache.wicket.markup.html.form.upload.FileUpload;

public class Certificate
{
	private String id;
	private List<FileUpload> file;

	public Certificate(String id)
	{
		this.id = id;
	}
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public List<FileUpload> getFile()
	{
		return file;
	}
	public void setFile(List<FileUpload> file)
	{
		this.file = file;
	}
}
