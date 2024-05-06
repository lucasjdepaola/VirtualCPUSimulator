/*
The bit class is about as fast as it can be without any native java bitwise functions
Branched bitwise functions slow down the iteration speed slightly.
100,000 number increment time: 249ms
10,000 addition operations time: 65ms
 */
class Bit {
    private Boolean value;

    public Bit(Boolean value) {
        this.value = value;
    }

    void set(Boolean value) {
        this.value = value;
    }

    Boolean getValue() {
        return value;
    }

    void toggle() {
        if(value)
            value = false;
        else
            value = true;
    }

    Bit and(Bit other) {
        if (value) {
            return new Bit(other.getValue());
        }
        else
            return new Bit(false);
    }

    Bit or(Bit other) {
        if (value) {
            return new Bit(true);
        }
        else if (other.getValue()) {
            return new Bit(true);
        }
        return new Bit(false);
    }

    Bit xor(Bit other) {
        if (value) {
            return other.not();
        } else if (other.getValue()) {
            return not();
        }
        else
            return new Bit(false);
    }

    Bit not() {
        if(value)
            return new Bit(false);
        else
            return new Bit(true);
    }

    void set() {
        value = true;
    }

    void clear() {
        value = false;
    }

    public String toString() {
        return value ? "t" : "f";
    }
}