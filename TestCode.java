class Animal {
    private String name;

    public Animal(String name) {
        this.name = name;
    }

    public void speak() {
        System.out.println(name + " makes a sound.");
    }

    // Getter for name (allows access from subclasses)
    protected String getName() {
        return name;
    }
}

class Dog extends Animal {
    public Dog(String name) {
        super(name); // Call the Animal constructor to initialize name
    }

    @Override //Indicates it's overriding a superclass method
    public void speak() {
        System.out.println("Dog says: Woof!");
    }

    public void fetch() {
        System.out.println(getName() + " is fetching."); // Use the getter
    }
}

public class OopBuggyTest {
    public static void main(String[] args) {
        Animal a = new Dog("Fido"); // Corrected, now uses a constructor
        a.speak(); // Now works correctly, calls Dog's overridden speak()

        Dog d = new Dog("Buddy"); // Corrected constructor
        d.speak(); //Calls the Dog speak() method
        d.fetch();
    }
}