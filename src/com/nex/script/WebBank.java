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
	public static String convertCoordinates(Area area) {

		String parsedCoordinates = "";
		for (Position tile : area.getTiles()) {
			if(parsedCoordinates.length() > 0)
				parsedCoordinates += ",";
			parsedCoordinates += String.format("{%d, %d}", tile.getX(), tile.getY());
		}
		return parsedCoordinates;
	}

	public static Position parseCoordinate(String parsedPosition) {
    	try {
			int index = 0;
			if (parsedPosition.startsWith("position")) {
				index += 1;
			}
			String[] parsedCoords = parsedPosition.split(";");
			int coordinate1 = Integer.parseInt(parsedCoords[index]);
			int coordinate2 = Integer.parseInt(parsedCoords[index + 1]);
			int coordinate3 = Integer.parseInt(parsedCoords[index + 2]);
			if (coordinate1 > 500 && coordinate2 > 500) {
				Position newPos = new Position(coordinate1, coordinate2, coordinate3);
				return newPos;
			}
		}catch (Exception ex) {}
		return null;
	}
	public static String convertCoordinate(Position pos) {
    	if(pos == null) return null;
		return String.format("position;%d;%d;%d", pos.getX(), pos.getY(),  pos.getFloorLevel());
	}
 

    public Area getArea() {
        return area;
    }
}