package com.fortyoneconcepts.valjogen.testsources;

import java.util.Objects;

public class BaseClass implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	protected final int baseValue;

	protected BaseClass(int baseValue)
	{
		this.baseValue=baseValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hashCode(baseValue);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		BaseClass other = (BaseClass) obj;

		if (!Objects.equals(baseValue, other.baseValue))
			return false;

		return true;
	}
}
