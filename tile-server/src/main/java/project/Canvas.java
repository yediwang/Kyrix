package project;

import java.util.ArrayList;

/**
 * Created by wenbo on 1/4/18.
 */
public class Canvas {

	private String id;
	private int w;
	private int h;
	private String wSql, hSql, wLayerId, hLayerId;
	private double zoomInFactorX, zoomInFactorY;
	private double zoomOutFactorX, zoomOutFactorY;
	private ArrayList<Transform> transforms;
	private ArrayList<Layer> layers;
	private String axes;

	public void setW(int w) {
		this.w = w;
	}

	public void setH(int h) {
		this.h = h;
	}

	public String getId() {
		return id;
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	public String getwSql() {
		return wSql;
	}

	public String gethSql() {
		return hSql;
	}

	public String getwLayerId() {
		return wLayerId;
	}

	public String gethLayerId() {
		return hLayerId;
	}

	public double getZoomInFactorX() {
		return zoomInFactorX;
	}

	public double getZoomInFactorY() {
		return zoomInFactorY;
	}

	public double getZoomOutFactorX() {
		return zoomOutFactorX;
	}

	public double getZoomOutFactorY() {
		return zoomOutFactorY;
	}

	public ArrayList<Transform> getTransforms() {
		return transforms;
	}

	public ArrayList<Layer> getLayers() {
		return layers;
	}

	public String getAxes() {
		return axes;
	}

	public Transform getTransformById(String id) {

		for (Transform t : transforms)
			if (t.getId().equals(id))
				return t;

		return null;
	}

	public String getDbByLayerId(String layerId) {
		String transformId = this.getLayers()
				.get(Integer.valueOf(layerId))
				.getTransformId();
		return this.getTransformById(transformId).getDb();
	}

	@Override
	public String toString() {
		return "Canvas{" +
				"id='" + id + '\'' +
				", w=" + w +
				", h=" + h +
				", wSql='" + wSql + '\'' +
				", hSql='" + hSql + '\'' +
				", wLayerId='" + wLayerId + '\'' +
				", hLayerId='" + hLayerId + '\'' +
				", zoomInFactorX=" + zoomInFactorX +
				", zoomInFactorY=" + zoomInFactorY +
				", zoomOutFactorX=" + zoomOutFactorX +
				", zoomOutFactorY=" + zoomOutFactorY +
				", transforms=" + transforms +
				", layers=" + layers +
				", axes='" + axes + '\'' +
				'}';
	}
}
