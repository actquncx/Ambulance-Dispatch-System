package model;

public class Patient {
    private int id;
    private String name;
    private String surname;
    private long phone;
    private int age;
    private String address;

    public Patient(String name, String surname, long phone, int age, String address) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.age = age;
        this.address = address;
    }

    // Геттери та Сеттери
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public long getPhone() { return phone; }
    public int getAge() { return age; }
    public String getAddress() { return address; }
}