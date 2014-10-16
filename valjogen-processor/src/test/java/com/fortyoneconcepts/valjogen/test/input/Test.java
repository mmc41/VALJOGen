package com.fortyoneconcepts.valjogen.test.input;

public class Test extends ComparableBaseClass
{
	private int z;

	public Test()
	{
		super(43, "bla");
		z=3;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Test other = (Test) obj;
		if (z != other.z)
			return false;
		return true;
	}


}
