package neopost2;

/**
 * Created by jhooba on 2016-09-18.
 */
public class Area implements Comparable<Area> {
  private final String name;
  private final int metric;

  public Area(String area) {
    this.name = area;
    float f = Float.parseFloat(area);
    this.metric = (int)(f * 1000);
  }

  public int getPyong() {
    float f = Float.parseFloat(name);
    return Math.round(f / 3.3f);
  }

  @Override
  public int hashCode() {
    return getPyong();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Area)) return false;

    Area area = (Area) o;
    if (metric != area.metric) return false;
    return name.equals(area.name);
  }

  @Override
  public int compareTo(Area o) {
    return metric - o.metric;
  }
}
