package com.meivaldi.adminwisatatanohgayo;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Place implements Serializable {

    private String alamat, deskripsi, jam_operasional, nama_tempat, sumber, ketinggian, luas, foto;
    private Double lat, lon;
    private Integer rating;
    private int id, tempat_terdekat;

    public Place() { }

    public Place(int id, String alamat, String deskripsi, String jam_operasional, String nama_tempat, String sumber,
                 String ketinggian, String luas, Double lat, Double lon, int rating, String foto, int tempat_terdekat) {
        this.id = id;
        this.alamat = alamat;
        this.deskripsi = deskripsi;
        this.jam_operasional = jam_operasional;
        this.nama_tempat = nama_tempat;
        this.sumber = sumber;
        this.ketinggian = ketinggian;
        this.luas = luas;
        this.lat = lat;
        this.lon = lon;
        this.rating = new Integer(rating);
        this.foto = foto;
        this.tempat_terdekat = tempat_terdekat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getJam_operasional() {
        return jam_operasional;
    }

    public void setJam_operasional(String jam_operasional) {
        this.jam_operasional = jam_operasional;
    }

    public String getNama_tempat() {
        return nama_tempat;
    }

    public void setNama_tempat(String nama_tempat) {
        this.nama_tempat = nama_tempat;
    }

    public String getSumber() {
        return sumber;
    }

    public void setSumber(String sumber) {
        this.sumber = sumber;
    }

    public String getKetinggian() {
        return ketinggian;
    }

    public void setKetinggian(String ketinggian) {
        this.ketinggian = ketinggian;
    }

    public String getLuas() {
        return luas;
    }

    public void setLuas(String luas) {
        this.luas = luas;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getTempat_terdekat() {
        return tempat_terdekat;
    }

    public void setTempat_terdekat(int tempat_terdekat) {
        this.tempat_terdekat = tempat_terdekat;
    }
}
