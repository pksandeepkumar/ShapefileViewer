/**
 * diewald_shapeFileReader.
 * 
 * a Java Library for reading ESRI-shapeFiles (*.shp, *.dfb, *.shx).
 * 
 * 
 * Copyright (c) 2012 Thomas Diewald
 *
 *
 * This source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License is available on the World
 * Wide Web at <http://www.gnu.org/copyleft/gpl.html>. You can also
 * obtain it by writing to the Free Software Foundation,
 * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */


package com.texus.shapefileviewer.shape.diewald_shapeFile.files.shp.shapeTypes;

import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * Shape: PolyLine.<br>
 * <pre>
 * polyline: consists of one or more parts.
 * part:     connected sequence of two or more points. 
 *           may or may not be connected to one another.
 *           may or may not intersect one another.
 * same content as polygon.
 * possible ShapeTypes:
 *   PolyLine   (  8 ), 
 *   PolyLineZ  ( 18 ), 
 *   PolyLineM  ( 28 ), 
 * </pre>
 * @author thomas diewald (2012)
 *
 */
public class ShpPolyLine extends ShpShape {
  
  // SHAPE RECORD CONTENT
  private double[][] SHP_bbox = new double[3][2]; // [x, y, z][min, max]
  private double[] SHP_range_m = new double[2];   // [min, max]

  private int    SHP_num_parts, SHP_num_points;
  private int[]  SHP_parts;
  private double[][] SHP_xyz_points; // [number of points][x,y,z]
  private double[] SHP_m_values;     // [number of points][m-value]

  private double[][][] parts = null; //[number of polygons][vertices][x, y, z, w]
  
  
  public ShpPolyLine(Type shape_type){
    super(shape_type);
  }
  
  @Override
  protected void readRecordContent(ByteBuffer bb){
    SHP_bbox[0][0] = bb.getDouble(); // x-min
    SHP_bbox[1][0] = bb.getDouble(); // y-min
    SHP_bbox[0][1] = bb.getDouble(); // x-max
    SHP_bbox[1][1] = bb.getDouble(); // y-max
    SHP_num_parts  = bb.getInt();    // number of polygon-parts / rings
    SHP_num_points = bb.getInt();    // number of points (total of all parts)
    
    SHP_parts = new int[SHP_num_parts];
    for(int i = 0; i < SHP_num_parts; i++){
      SHP_parts[i] = bb.getInt(); // index of the point-list (indicates start-point of a polygon)
    }
  
    SHP_xyz_points = new double[SHP_num_points][3];
    for(int i = 0; i < SHP_num_points; i++){
      SHP_xyz_points[i][0] = bb.getDouble(); // x - coordinate
      SHP_xyz_points[i][1] = bb.getDouble(); // y - coordinate
    }
    
    // if SHAPE-TYPE: 13
    if( shape_type.hasZvalues() ){
      SHP_bbox[2][0] = bb.getDouble(); // z-min
      SHP_bbox[2][1] = bb.getDouble(); // z-max
      for(int i = 0; i < SHP_num_points; i++){
        SHP_xyz_points[i][2] = bb.getDouble(); // z - coordinate
      }
    }
    
    // if SHAPE-TYPE: 13 | 23
    if( shape_type.hasMvalues() ){
      SHP_range_m[0] = bb.getDouble(); // m-min
      SHP_range_m[1] = bb.getDouble(); // m-max
      SHP_m_values = new double[SHP_num_points];
      for(int i = 0; i < SHP_num_points; i++){
        SHP_m_values[i] = bb.getDouble(); // m - value
      }
    }
  }
  
  @Override 
  public void print(){
    System.out.printf(Locale.ENGLISH, "   _ _ _ _ _ \n");
    System.out.printf(Locale.ENGLISH, "  / SHAPE   \\_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n");
    System.out.printf(Locale.ENGLISH, "  |                                                    \\\n");
    System.out.printf(Locale.ENGLISH, "  |  <RECORD HEADER>\n");
    System.out.printf(Locale.ENGLISH, "  |    SHP_record_number       = %d\n", SHP_record_number);
    System.out.printf(Locale.ENGLISH, "  |    SHP_content_length      = %d bytes  (check: start/end/size = %d/%d/%d)\n", SHP_content_length*2, position_start, position_end, content_length);
    System.out.printf(Locale.ENGLISH, "  |\n");
    System.out.printf(Locale.ENGLISH, "  |  <RECORD CONTENT>\n");
    System.out.printf(Locale.ENGLISH, "  |    shape_type              = %s (%d)\n", shape_type, shape_type.ID() );
    System.out.printf(Locale.ENGLISH, "  |    SHP_bbox: xmin, xmax    = %+7.3f, %+7.3f\n", SHP_bbox[0][0], SHP_bbox[0][1]);
    System.out.printf(Locale.ENGLISH, "  |    SHP_bbox: ymin, ymax    = %+7.3f, %+7.3f\n", SHP_bbox[1][0], SHP_bbox[1][1]);
    System.out.printf(Locale.ENGLISH, "  |    SHP_bbox: zmin, zmax    = %+7.3f, %+7.3f\n", SHP_bbox[2][0], SHP_bbox[2][1]);
    System.out.printf(Locale.ENGLISH, "  |    SHP_measure: mmin, mmax = %+7.3f, %+7.3f\n", SHP_range_m[0], SHP_range_m[1]);
    System.out.printf(Locale.ENGLISH, "  |    SHP_num_parts           = %d\n", SHP_num_parts  );
    System.out.printf(Locale.ENGLISH, "  |    SHP_num_points          = %d\n", SHP_num_points );
//    for(int i = 0; i < SHP_num_parts; i++){
//      System.out.printf(Locale.ENGLISH, "  |     part_idx[%d] = %d\n", i, SHP_parts[i] );
//    }
//    
    System.out.printf(Locale.ENGLISH, "  \\_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ /\n");
  }

  
  
  
  
  
  
  
  
  
  
  
  
  /** 
   * get the BoundingBox..<br>
   * data storage: [x, y, z][min, max]  <br>
   * @return 2d-array (double), dim-size:[3][2]
   */
  public double[][] getBoundingBox(){
    return SHP_bbox;
  }

  /**
   * get range of Measure-Values.<br>
   * data storage: [min, max]  <br>
   * @return 1d-array (double), dim-size:[2]
   */
  public double[] getMeasureRange(){
    return SHP_range_m;
  }
  
  
  /**
   * get the number of points(vertices).
   * @return the number of points(vertices).
   */
  public int getNumberOfPoints(){
    return SHP_num_points;
  }
  /**
   * get the number of parts(Polygons)
   * @return the number of parts(Polygons).
   */
  public int getNumberOfParts(){
    return SHP_num_parts;
  }
  
  /**
   * get an array of all points(vertices).
   * @return an array of all points(vertices).
   */
  public double[][] getPoints(){
    return SHP_xyz_points;
  }
  

  /**
   * generates a list of polylines, and returns a 3d-double array.<br>
   * [number of polylines][number of points per polyline][x, y, z, m].
   * 
   * @return 3d-double array.
   */
  public double[][][] getPointsAs3DArray(){
    // if the method was called before, we already have the array.
    if( parts != null ){
      return parts;
    }
    
    int[] indices = new int[SHP_num_parts+1];                  // generate new indices array
    System.arraycopy(SHP_parts, 0, indices, 0, SHP_num_parts); // copy start indices
    indices[indices.length-1] = SHP_num_points;                // and add last index
    
    parts = new double[SHP_num_parts][][];
    for(int i = 0; i < indices.length-1; i++){
      int from = indices[i];   // start index
      int to   = indices[i+1]; // end-index + 1
      int size = to-from;
      parts[i] = new double[size][4];
      for(int j = from, idx = 0; j < to; j++, idx++){
        parts[i][idx][0] = SHP_xyz_points[j][0]; // copy of x-value
        parts[i][idx][1] = SHP_xyz_points[j][1]; // copy of y-value
        parts[i][idx][2] = SHP_xyz_points[j][2]; // copy of z-value
        if( shape_type.hasMvalues() ){
          parts[i][idx][3] = SHP_m_values[j];      // copy of m-value
        }
      }
    }
    return parts;
  }
  
  
  /**
   * get the Measure Values as an Array.
   * @return measure-values. (size=.getNumberOfPoints()).
   */
  public double[] getMeasureValues(){
    return SHP_m_values;
  }
  
  
  
  
  
  
  
}
