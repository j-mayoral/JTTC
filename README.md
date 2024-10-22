 JTTC: Java Type system is Turing Complete
 ---

<!-- TOC -->
  * [JTTC: Java Type system is Turing Complete](#jttc-java-type-system-is-turing-complete)
  * [Turing Machines to Java Generics](#turing-machines-to-java-generics)
  * [Definition of Turing machine](#definition-of-turing-machine)
  * [Installation](#installation)
  * [Usage](#usage)
  * [Examples](#examples)
    * [BAB](#bab)
    * [Infinite](#infinite)
    * [Palindrome](#palindrome)
    * [Eq](#eq)
  * [References](#references)
* [TODO](#todo)
<!-- TOC -->

## Turing Machines to Java Generics

The present project is a _toy_ that implements the ideas of the
paper [java generics are turing complete](doc/java-generics-are-turing-complete.pdf).
Essentially, it takes the definition of a Turing machine and generates a set of
interfaces and methods that emulate the machine and its alphabet, respectively.
Thus, we have:

- A Turing machine `T`, with associated Java interfaces `Code(T)`.

- An input `a_1 a_2 a_3...`, with a method call of the form
  `start.a_1().a_2()...stop()`. This call will have a certain Java type, `S`.
- A Java type `Z` and a `run` method with signature

```java
  public Z run() {
  return start.a_1().a_2()...stop();
}
```

With this, `T` will accept the input `a_1 a_2 a_3...` if, and only if, the Java
compiler, with `Code(T)`, accepts the above call signature.
That is, if `S` is a subtype of `Z` (`S <: Z`).

## Definition of Turing machine

This project uses the following ingredients to define a Turing machine:

**turing machine:** _a turing machine `T` is a tuple of the form
`(Q, init, halt, E, d)` with:_

- _`Q`: a collection of states_.
- _`init`: the initial distinguished state_.
- _`halt`: the distinguished halt state_
- _`E`: the alphabet of symbols, along with a distinguished one for the
  blank symbol_.
- _`d`: the set of transition functions of the form `Q x E -> Q x E x {R, L}`._

Using this, the files that this repo accepts as valid definitions
of turing machines have the following syntax:

```
<MACHINE NAME> {
    INITIAL_STATE: <INITIAL STATE>
    HALT_STATE: <HALT STATE>
    STATES: <A LIST WITH THE REST OF STATES>
    BLANK_SYMBOL: <A SYMBOL FOR BLANK>
    ALPHABET: <A LIST WITH THE REST OF ALPHABET SYMBOLS>
    TRANSITIONS {
        <LIST OF TRANSITIONS OF THE FORM (STATE, SYMBOL) -> (STATE, SYMBOL, R OR L)>
    }
```

The order in which the fields are defined must be as specified.
This is an example of a machine that recognizes whether a string of
letters of `a`s and `b`s contains the substring `bab`:

```
BAB {
    INITIAL_STATE: init
    HALT_STATE: halt
    STATES: (q0, q1, q2)
    BLANK_SYMBOL: $
    ALPHABET: (a, b)
    TRANSITIONS {
        (init, $) -> (q0, $, R),
        (q0, a) -> (q0, a, R),
        (q0, b) -> (q1, b, R),
        (q1, a) -> (q2, a, R),
        (q1, b) -> (q1, b, R),
        (q2, a) -> (q0, a, R),
        (q2, b) -> (halt, b, R)
    }
}
```

As a detail, it is important that the names used: machine name,
states and symbols are names that Java can accept.

Check these other examples [here](examples).

## Installation

Clone the repo and build it: 

```sh
git clone git@github.com:j-mayoral/JTTC.git
```

```
cd JTTC
./gradlew clean build
```

If it output `BUILD SUCCESSFUL` everything is fine!

## Usage

Once the repository is downloaded and compiled, we can use it by running

```sh
./gradlew run --args="<path-to-turing-machine-definition-file> <output-path> <input>"
```

- `path-to-turing-machine-definition-file`: path to machine definition with the
  same structure as the one given
  in [the previous definition](#definition-of-turing-machine).
- `output-path`: destination path where the generated Java code will be placed.
- `input`: input string on which the machine will run.

After executing the above command, you should find a file at `output-path` with
the same name as the machine but this time with the `.java` extension.

To check the result of running the machine, we compile the file
`.java`:

```sh
javac -d gen <machine-name>.java
```

If the machine ends up in an accepting state, the file will compile, otherwise,
it will give a compilation error.

There is also a `help` option: 

```shell
./gradlew run --args="help"
```

## Examples

In [this folder](examples) we have some definitions to play with.

### BAB

This is the simplest example we have. This machine recognizes if a string
formed by `a`s and `b`s contains the substring `bab`. For this, we have the
definition:

```
BAB {
    STATES: (qs, q0, q1, q2, q3)
    ALPHABET: (a, b, $)
    TRANSITIONS {
        (qs, $) -> (q0, $, R),
        (q0, a) -> (q0, a, R),
        (q0, b) -> (q1, b, R),
        (q1, a) -> (q2, a, R),
        (q1, b) -> (q1, b, R),
        (q2, a) -> (q0, a, R),
        (q2, b) -> (q3, b, R)
    }
}
```

and can be compiled with:

```shell
 ./gradlew run --args="examples/bab.trng examples/output a a b a b a a" 
```

In the `examples/output` folder will be a `BAB.java` file that starts as:

```java
package output;

public abstract class BAB<T> {
  public E<? super E<? super Z>> run() {
    return start.a().a().b().a().b().a().a().stop();
  }
  // CONTINUE...
}
```

We can try to compile this file, which will be successful because
` a().a().a().b().a().a().a()` contains the string `b().a().a().b()`.
The following command will populate the gen folder with the compiled classes:

```shell
javac examples/output/BAB.java -d examples/output/gen
```

If, on the other hand, we edit the file and change the fragment of the string
with one that does not contain `b().a().b()`:

```java
package output;

public abstract class BAB<T> {
  public E<? super E<? super Z>> run() {
    return start.a().a().a().b().a().a().stop(); // no bab substring
  }
  //SAME AS BEFORE
}
```

In this case we will get a compilation error (absolutely incomprehensible):

```
examples/output/BAB.java:5: error: incompatible types: qsWR<CAP#1> cannot be converted to E<? super E<? super Z>>
return start.a().a().a().b().a().a().stop();
                                         ^
  where CAP#1 is a fresh type-variable:
    CAP#1 extends Object super: L$<? super N<? super La<? super N<? super La<? 
    super N<? super Lb<? super N<? super La<? super N<? super La<? super N<? super
     La<? super N<? super ML<? super N<? super L$<? super N<? super E<? super 
     E<? super Z>>>>>>>>>>>>>>>>>>>> from capture of ? super L$<? super N<? super 
     La<? super N<? super La<? super N<? super Lb<? super N<? super La<? super
      N<? super La<? super N<? super La<? super N<? super ML<? super N<? 
      super L$<? super N<? super E<? super E<? super Z>>>>>>>>>>>>>>>>>>>>
1 error
```

### Infinite

This is a silly example of a Turing machine that never stops:

```
infinite {
    INITIAL_STATE: init
    HALT_STATE: halt
	STATES: (q1)
	BLANK_SYMBOL: $
	ALPHABET: (a)
	TRANSITIONS {
		(init, $) -> (q1, $, R),
		(init, a) -> (q1, a, R),
		(q1, a) -> (init, a, L)
	}
}
```

If we compile the generated file we will get a `StackOverflow` because the
typing process enters in an infinite loop:

```shell
 ./gradlew run --args="examples/infinite.trng examples/output a "
```

```shell
 javac examples/output/infinite.java -d gen


The system is out of resources.
Consult the following stack trace for details.
java.lang.StackOverflowError

```

### Palindrome

This machine detects, for a two-letter alphabet (`a` and `b`), whether the
string is a palindrome.

### Eq

This machine detects if two strings are the same. The alphabet is about two
letters (`a` and `b`) and a distinguished sign `eq` wich separates the two
strings to check the equality.

```shell
 ./gradlew run --args="examples/eq.trng examples/output a b a eq a b a" 
```

Again, since these strings are the same, it will compile:

```shell
javac -J-Xss8m examplesOutput/eq.java -d gen
```

Just note that we need to increase the memory for it to compile smoothly.

## References

- [java generics are turing complete](doc/java-generics-are-turing-complete.pdf)
- [subtyping variance](doc/subtyping-variance.pdf)

----

# TODO

- [ ] Add more machines.
- [ ] Accept context free grammars as definitions.

