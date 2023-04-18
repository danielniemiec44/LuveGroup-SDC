package luvegroup.sdc;

public class Contact {
    private String name;
    private String surname;
    private String[] phones = new String[5];
    private int raw_id;

    public Contact(String name, String surname, String... phones) {
        this.name = name;
        this.surname = surname;
        this.phones = phones;
    }

    public Contact() {

    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String[] getPhones() {
        return phones;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPhones(String[] phones) {
        this.phones = phones;
    }
}
