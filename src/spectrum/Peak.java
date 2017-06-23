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

package spectrum;

/**
 * <p>This Class represents one peak of a spectrum (mainly m/z and intensity).
 * <p>Previous name: DataPoint
 */
public class Peak {

    /** The M/Z. */
    double mz=0;
    
    /** The Intensity(not necessarily the absolute one). */
    double Intensity=0;
    
    /** The relative intensity. */
    double relativeIntens=-10;
    
    /** The absolute intensity. */
    double absoluteIntens = 0;
    
    /** The mz offset. ?? */
    //double mzOffset=10;
    
    /**
     * Instantiates a new peak.
     */
    public Peak(){
        
    }
    
    /**
     * Instantiates a new peak.
     *
     * @param mz the mz
     * @param intens the intensity
     */
    public Peak(double mz,double intens){
        this.mz=mz;
        this.Intensity=intens;
    }
    
    /**
     * Sets the mz.
     *
     * @param mz the new mz
     */
    public void setMz(double mz){
        this.mz=mz;
    }
    
    /**
     * Gets the mz.
     *
     * @return the mz
     */
    public double getMz(){
        return this.mz;
    }
    
    /**
     * Sets the intensity.
     *
     * @param intens the new intensity
     */
    public void setIntensity(double intens){
        this.Intensity=intens;
    }
    
    /**
     * Gets the intensity.
     *
     * @return the intensity
     */
    public double getIntensity(){
        return this.Intensity;
    }
    
    /**
     * Sets the relative intens.
     *
     * @param relativeIntens the new relative intens
     */
    public void setRelativeIntens(double relativeIntens){
        this.relativeIntens=relativeIntens;
    }
    
    /**
     * Gets the relative intens.
     *
     * @return the relative intens
     */
    public double getRelativeIntens(){
        return this.relativeIntens;
    }
    
    public void setAbsoluteIntens(double intens){
        this.absoluteIntens=intens;
    }
    
    public double getAbsoluteIntens(){
        return this.absoluteIntens;
    }
    /**
     * Sets the mz offset.
     *
     * @param offSet the new mz offset
     */
   // public void setMzOffset(double offSet){
        //this.mzOffset=offSet;
    //}
    
    /**
     * Gets the mz offset.
     *
     * @return the mz offset
     */
    //public double getMzOffset(){
      //  return this.mzOffset;
    //}

}
