package my.aitest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JsonDocument {

  @JsonProperty("accession_number")
  private String accessionNumber;

  private String artist;
  private String classification;
  private String continent;
  private String country;
  private String creditline;
  private String culture;

  @JsonProperty("curator_approved")
  private Integer curatorApproved;

  private String dated;
  private String department;
  private String description;
  private String dimension;
  private Long id;
  private String image;

  @JsonProperty("image_height")
  private Integer imageHeight;

  @JsonProperty("image_width")
  private Integer imageWidth;

  @JsonProperty("image_copyright")
  private String imageCopyright;

  private String inscription;

  @JsonProperty("life_date")
  private String lifeDate;

  private String markings;
  private String medium;
  private String nationality;

  @JsonProperty("object_name")
  private String objectName;

  private String portfolio;
  private String provenance;
  private Integer restricted;

  @JsonProperty("rights_type")
  private String rightsType;

  private String room;
  private String signed;
  private String style;
  private String text;
  private String title;

}