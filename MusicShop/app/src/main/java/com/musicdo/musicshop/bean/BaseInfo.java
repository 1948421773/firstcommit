package com.musicdo.musicshop.bean;


public class BaseInfo
{
	protected String Id;
	protected String name;
	protected boolean isChoosed;

	protected boolean isEdited;

	protected boolean isAllEdited;

	public String getEditString() {
		return EditString;
	}

	public void setEditString(String editString) {
		EditString = editString;
	}

	protected String EditString;

	public BaseInfo()
	{
		super();
	}

	public BaseInfo(String id, String name,boolean isEdited,boolean isAllEdited)
	{
		super();
		Id = id;
		this.name = name;
		this.isEdited = isEdited;
		this.isAllEdited = isAllEdited;

	}

	public String getId()
	{
		return Id;
	}

	public void setId(String id)
	{
		Id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isChoosed()
	{
		return isChoosed;
	}

	public void setChoosed(boolean isChoosed)
	{
		this.isChoosed = isChoosed;
	}

	public boolean isEdited() {
		return isEdited;
	}

	public void setEdited(boolean edited) {
		isEdited = edited;
	}

	public boolean isAllEdited() {
		return isAllEdited;
	}

	public void setAllEdited(boolean allEdited) {
		isAllEdited = allEdited;
	}
}
