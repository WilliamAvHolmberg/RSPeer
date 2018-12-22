package com.nex.script;

import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;

public enum WebBank {


	;
    private final Area area;

    WebBank(Area area) {
        this.area = area;
    }

 
    
    public static Area parseCoordinates(String parsedActionArea) {
    	String parsedCoordinates = "";

		// if last, do not add ":"
		for (String coord : parsedActionArea.split("\\{")) {
			String newCoord = coord.replaceAll("\\},", "").replaceAll("\\}", "").replaceAll(" ", "");
			if (newCoord.length() > 3) {
				parsedCoordinates = parsedCoordinates + newCoord + ":";
			}
		}
		Position[] newCoordinates = new Position[parsedCoordinates.split(":").length];
		String[] almostReadyCoordinates = parsedCoordinates.split(":");
		for (int i = 0; i < almostReadyCoordinates.length; i++) {
			String[] parsedCoords = almostReadyCoordinates[i].split(",");
			int coordinate1 = Integer.parseInt(parsedCoords[0]);
			int coordinate2 = Integer.parseInt(parsedCoords[1]);
			if (coordinate1 > 500 && coordinate2 > 500) {
				Position newPos = new Position(coordinate1, coordinate2,0);
				newCoordinates[i] = newPos;
			}
		}
		return Area.polygonal(newCoordinates);
    }

 

    public Area getArea() {
        return area;
    }
}