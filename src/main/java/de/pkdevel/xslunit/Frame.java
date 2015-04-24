package de.pkdevel.xslunit;

import java.util.prefs.Preferences;

import javafx.stage.Stage;

final class Frame {
	
	private static final Frame DEFAULT = new Frame();
	
	final double x, y, width, height;
	
	Frame() {
		this.x = 100;
		this.y = 100;
		this.width = 800;
		this.height = 600;
	}
	
	Frame(final double x, final double y, final double width, final double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	boolean isDefault() {
		return this == DEFAULT;
	}
	
	static Frame fromStage(final Stage stage) {
		return new Frame(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
	}
	
	static Frame fromDefaults() {
		return DEFAULT;
	}
	
	static Frame fromPreferences(final Preferences userPrefs) {
		if (userPrefs != null) {
			final double x = userPrefs.getDouble("stage.x", 0);
			final double y = userPrefs.getDouble("stage.y", 0);
			final double width = userPrefs.getDouble("stage.width", 0);
			final double height = userPrefs.getDouble("stage.height", 0);
			
			if (x != 0 && y != 0 && width != 0 && height != 0) {
				return new Frame(x, y, width, height);
			}
		}
		
		return DEFAULT;
	}
	
	@Override
	public String toString() {
		return "Frame [x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(this.height);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(this.width);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(this.x);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(this.y);
		result = prime * result + (int) (temp ^ temp >>> 32);
		return result;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Frame other = (Frame) obj;
		if (Double.doubleToLongBits(this.height) != Double.doubleToLongBits(other.height)) {
			return false;
		}
		if (Double.doubleToLongBits(this.width) != Double.doubleToLongBits(other.width)) {
			return false;
		}
		if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
			return false;
		}
		return true;
	}
	
}
