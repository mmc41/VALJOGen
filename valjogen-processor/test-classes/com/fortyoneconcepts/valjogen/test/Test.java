package com.fortyoneconcepts.valjogen.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Test {
	public char c;
	public byte b;
	public int i;
	public long l;
	public double d;
	public double d2;
	public float f;
	public Float f2;
	public String x;
	public Object o;
	public TestEnum e;
	public List<String> sList;
	public Map<String,String> sMap;
	public byte[] bA;
	public Test[] testAry;


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + b;
		result = prime * result + Arrays.hashCode(bA);
		result = prime * result + c;
		long temp;
		temp = Double.doubleToLongBits(d);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(d2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((e == null) ? 0 : e.hashCode());
		result = prime * result + Float.floatToIntBits(f);
		result = prime * result + ((f2 == null) ? 0 : f2.hashCode());
		result = prime * result + i;
		result = prime * result + (int) (l ^ (l >>> 32));
		result = prime * result + ((o == null) ? 0 : o.hashCode());
		result = prime * result + ((sList == null) ? 0 : sList.hashCode());
		result = prime * result + ((sMap == null) ? 0 : sMap.hashCode());
		result = prime * result + Arrays.hashCode(testAry);
		result = prime * result + ((x == null) ? 0 : x.hashCode());
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
		Test other = (Test) obj;
		if (b != other.b)
			return false;
		if (!Arrays.equals(bA, other.bA))
			return false;
		if (c != other.c)
			return false;
		if (Double.doubleToLongBits(d) != Double.doubleToLongBits(other.d))
			return false;
		if (Double.doubleToLongBits(d2) != Double.doubleToLongBits(other.d2))
			return false;
		if (e != other.e)
			return false;
		if (Float.floatToIntBits(f) != Float.floatToIntBits(other.f))
			return false;
		if (f2 == null) {
			if (other.f2 != null)
				return false;
		} else if (!f2.equals(other.f2))
			return false;
		if (i != other.i)
			return false;
		if (l != other.l)
			return false;
		if (o == null) {
			if (other.o != null)
				return false;
		} else if (!o.equals(other.o))
			return false;
		if (sList == null) {
			if (other.sList != null)
				return false;
		} else if (!sList.equals(other.sList))
			return false;
		if (sMap == null) {
			if (other.sMap != null)
				return false;
		} else if (!sMap.equals(other.sMap))
			return false;
		if (!Arrays.equals(testAry, other.testAry))
			return false;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		return true;
	}




}
