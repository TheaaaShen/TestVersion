/*
 * Copyright 2006-2011 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package util;

/**
 * This interface represents one datapoint of a spectra (m/z and intensity)
 */
public class DataPoint {

	double mz=0;
	double Intensity=0;
	double relativeIntens=-10;
	double mzOffset=10;
	public DataPoint()
	{
		
	}
	
	public DataPoint(double mz,double intens)
	{
		this.mz=mz;
		this.Intensity=intens;
	}
    public double getMZ()
    {
    	return this.mz;
    }
    public double getIntensity()
    {
    	return this.Intensity;
    }
    public void setMZ(double mz)
    {
    	this.mz=mz;
    }
    public void setIntensity(double intens)
    {
    	this.Intensity=intens;
    }
    public void setRelativeIntens(double relativeIntens)
    {
    	this.relativeIntens=relativeIntens;
    }
    public double getRelativeIntens()
    {
    	return this.relativeIntens;
    }
    public void setMzOffset(double offSet)
    {
    	this.mzOffset=offSet;
    }
    public double getMzOffset()
    {
    	return this.mzOffset;
    }

}
