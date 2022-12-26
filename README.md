# Greg
Greg is a Java project that can be used to create a calendar of any given year and German federal state using the "Itemis AG" colours and icon.

## Usage
Simply change the integer value for year in the main method and select the desired enum value for your federal state from the federal state method. After you've run the main method an svg file will appear in your IDE as well as locally to be opened in any desired editor.

## Motivation
Instead of the Batik SVG library, I decided to use JAXB to render templates for the most important objects for the calender:
  - rectangle element
  - text element
  - group element consisting of a rectangle and multiple text elements 
  - svg element (to display the icon)
  
New templates need to be added to the "SVG Calender" class.

Unfortunatly, the JAXB Marshaller runs into an issue when dealing with a string that entails HTML structured text and safeguards the tags ("<, >") to the entities "&lt;" and "&gt;". Fixing this with a DOM handler or communicating this to the Marshaller in a different more performant way fails with the knowledge of contributors of JAXB.
