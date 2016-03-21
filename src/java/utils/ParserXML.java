/**Copyright 2016, University of Messina.
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/


package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class ParserXML
{
  private Element rootElement;
  Document document;

    public Element getRootElement() {
        return rootElement;
    }


  public void modifyXML( final String tag, final String value )
  {
    rootElement.getChild( tag ).setText(  value );
  }

  public void saveXML( final String filename )
  {
    try
    {
      OutputStream out = new FileOutputStream( filename );
      XMLOutputter xmlOutput=new XMLOutputter();
      xmlOutput.setFormat(Format.getPrettyFormat());
      xmlOutput.output(document, out);
      //new XMLOutputter().output( document, out );
    }
   
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }
  
  public ParserXML(File file){
        try {
            SAXBuilder builder = new SAXBuilder();
            document = (Document) builder.build( file );
            rootElement = document.getRootElement();
        }  catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }

  
  public ParserXML(InputStream is){
        try {
            SAXBuilder builder = new SAXBuilder();
            document = (Document) builder.build( is );
            rootElement = document.getRootElement();
            
        } catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }
  
  
  public ParserXML( String XMLString )
  {
    // TODO put here the InputStream and the call xmlToString
    SAXBuilder builder = new SAXBuilder();

    try
    {
      document = builder.build( new StringReader( XMLString ) );
      rootElement = document.getRootElement();
    }
     catch( Exception ex )
    {
      ex.printStackTrace();
    }
    
  }
  
  public String getElementContent( String element )
  {
    return rootElement.getChildText( element );

  }
  
  
  public String getElementContent( String element, int istanceNumber )
  {
    List listOfTags = rootElement.getChildren( element );
    Element target = ( Element ) listOfTags.get( istanceNumber );
    return target.getText();

  }
  
 

  public String getElementAttributeContent( String element, String attribute )
  {
    if ( element.compareTo( rootElement.getName() ) == 0 )
    {
      return rootElement.getAttributeValue( attribute );
    }
    else
    {
      Element elem = rootElement.getChild( element );
      if(elem==null)
          return null;
      return elem.getAttributeValue( attribute );
    }
  }

  public String getStringedSubTree( String element ) throws JDOMException, IOException
  {
    String s = "";
    Element elem = rootElement.getChild( element );
    Iterator lst = elem.getDescendants();
    int i;
    Element e = document.detachRootElement();
    e.removeNamespaceDeclaration( Namespace.NO_NAMESPACE );
    Element e2 = e.getChild( element );
    XMLOutputter xout = new XMLOutputter();
    Format f = Format.getPrettyFormat();
    xout.setFormat( f );
    return ( ( xout.outputString( e2 ).replaceAll( "<" + element + ">", "" ) ).replaceAll( "</" + element + ">", "" ) );
  }

  public String getElementAttributeContent( String element, String attribute, int istanceNumber )
  {
    List listOfTags = rootElement.getChildren( element );
    Element target = ( Element ) listOfTags.get( istanceNumber );
    return target.getAttributeValue( attribute );

  }

  public int getElementNumber( String element ) throws JDOMException, IOException
  {
    return rootElement.getChildren( element ).size();
  }

  /*public boolean exist(String element){
  Element elem = null;
  elem = rootElement.getChild(element);
  if(elem == null){
  return(false);
  }
  else
  return(true);
  }*/
  public boolean exist( String element )
  {
    if ( rootElement.getName().compareTo( element ) == 0 || rootElement.getChildren( element ).size() != 0 )
    {
      return true;
    }
    else
    {
      return false;
    }
  }
  
  public Document getDocument(){
      return this.document;
  }
    //OVERLOADS
    public String getElementContent(String element, String defaultValue) {
        String returnV = "";
        returnV = rootElement.getChildText(element);
        if (returnV == null) {
            returnV = defaultValue;
        }
        return returnV;
    }

    public String getElementContent(String element, int istanceNumber, String defaultValue) {
        String returnV = "";
        List listOfTags = rootElement.getChildren(element);
        Element target = (Element) listOfTags.get(istanceNumber);
        returnV = target.getText();
        if (returnV == null) {
            returnV = defaultValue;
        }
        return returnV;
    }

    public String getElementAttributeContent(String element, String attribute, String defaultValue) {
        if (element.compareTo(rootElement.getName()) == 0) {
            return rootElement.getAttributeValue(attribute);
        } else {
            Element elem = rootElement.getChild(element);
            if (elem == null) {
                return defaultValue;
            }
            return elem.getAttributeValue(attribute);
        }
    }

    public String getElementAttributeContent(String element, String attribute, int istanceNumber, String defaultValue) {
        String returnV = "";
        List listOfTags = rootElement.getChildren(element);
        Element target = (Element) listOfTags.get(istanceNumber);
        returnV = target.getAttributeValue(attribute);
        if (returnV == null) {
            returnV = defaultValue;
        }
        return returnV;
    }
    
    
    public void printElementContentText(){
        for(int i =0;i<this.rootElement.getChildren().size();i++)
        {
            System.out.println("name "+((Element)this.rootElement.getChildren().get(i)).getName());
            System.out.println("text "+((Element)this.rootElement.getChildren().get(i)).getText());
        }
    }
    
    
      public static Element getElementByAttribute(Document document, String attributeId,String attributeValue){
   Element root, elemento;
   List listaElementi;
  
   
   root=document.getRootElement();
   listaElementi=root.getChildren();

   
   for(int i=0;i<listaElementi.size();i++){
       
        elemento=(Element)listaElementi.get(i);
        try{
            if(elemento.getAttributeValue(attributeId).equals(attributeValue)){
       
                return elemento;
            }
        }
        catch(NullPointerException ex){
        return null;
        }
   }
  return null;
  }

  
    /**
     * search recursuvely the element and returns the contents
     * @param element name of element
     * @return value of element, null if element don't exist
     */
    public String getElementContentInStructure( String element ){
        
        String value= rootElement.getChildText( element );
        Element result;
        if(value==null){
            result=this.searchElement(rootElement.getChildren(), element);
            if(result!=null){
                    value=result.getText();
            }
        }
    
        return value;
  }
 
    private Element searchElement(List<Element> listChildren,String elementName){
        
       Iterator iterElem;
        Element value=null;
        Element tmp;
                
        if(!listChildren.isEmpty()){
            iterElem=listChildren.iterator();
            while(iterElem.hasNext()&&value==null){
                tmp=((Element)iterElem.next());
                value=tmp.getChild(elementName);
                if(value==null){
                    value=searchElement(tmp.getChildren(),elementName);
                    }
                 }
         }
        
         return value;
        
        
    }
   
   public Element getElementInStructure( String element ){
        
        Element value= rootElement.getChild(element);
        if(value==null){
            value=this.searchElement(rootElement.getChildren(), element);
     
        }
    
        return value;
  }
   
  public ParserXML( Element rootElement ){ 
      
      this.rootElement =rootElement;
      
    } 

   public Element getElementByAttribute( String attributeId,String attributeValue){
   Element  elemento;
   List listaElementi;
  
   
   listaElementi=rootElement.getChildren();

   
   for(int i=0;i<listaElementi.size();i++){
       
        elemento=(Element)listaElementi.get(i);
        try{
            if(elemento.getAttributeValue(attributeId).equals(attributeValue)){
       
                return elemento;
            }
        }
        catch(NullPointerException ex){
        return null;
        }
   }
  return null;
  }
   
    /**
     * search recursuvely the element and returns the contents
     * @param root
     * @param element name of element
     * @return value of element, null if element don't exist
     */
    public static String getElementContentInStructure(Element root, String element ){
        
        String value= root.getChildText( element );
        Element result;
        if(value==null){
            result=ParserXML.searchElementStatic(root.getChildren(), element);
            if(result!=null){
                    value=result.getText();
            }
        }
    
        return value;
  }
 
    private static Element searchElementStatic(List<Element> listChildren,String elementName){
        
       Iterator iterElem;
        Element value=null;
        Element tmp;
                
        if(!listChildren.isEmpty()){
            iterElem=listChildren.iterator();
            while(iterElem.hasNext()&&value==null){
                tmp=((Element)iterElem.next());
                value=tmp.getChild(elementName);
                if(value==null){
                    value=searchElementStatic(tmp.getChildren(),elementName);
                    }
                 }
         }
        
         return value;
        
        
    }
   
   public static Element getElementInStructure(Element root, String element ){
        
        Element value= root.getChild(element);
        if(value==null){
            value=ParserXML.searchElementStatic(root.getChildren(), element);
     
        }
    
        return value;
  }
   
  public static Element getElementByAttribute(Element root, String attributeId,String attributeValue){
   Element elemento;
   List listaElementi;
  
   
   listaElementi=root.getChildren();

   
   for(int i=0;i<listaElementi.size();i++){
       
        elemento=(Element)listaElementi.get(i);
        try{
            if(elemento.getAttributeValue(attributeId).equals(attributeValue)){
       
                return elemento;
            }
        }
        catch(NullPointerException ex){
        return null;
        }
   }
  return null;
  }

  
    public static Element getElementByAttribute(Document document, String attributeId,String attributeValue,Namespace namespace){
   Element root, elemento;
   List listaElementi;
  
   
   root=document.getRootElement();
   listaElementi=root.getChildren();

   
   for(int i=0;i<listaElementi.size();i++){
       
        elemento=(Element)listaElementi.get(i);
        try{
            if(elemento.getAttributeValue(attributeId, namespace).equals(attributeValue)){
       
                return elemento;
            }
        }
        catch(NullPointerException ex){
        return null;
        }
   }
  return null;
  }

  
    /**
     * search recursuvely the element and returns the contents
     * @param element name of element
     * @return value of element, null if element don't exist
     */
    public String getElementContentInStructure( String element, Namespace namespace ){
        
        String value= rootElement.getChildText( element,namespace );
        Element result;
        if(value==null){
            result=this.searchElement(rootElement.getChildren(), element, namespace);
            if(result!=null){
                    value=result.getText();
            }
        }
    
        return value;
  }
 
    private Element searchElement(List<Element> listChildren,String elementName,Namespace namespace){
        
       Iterator iterElem;
        Element value=null;
        Element tmp;
                
        if(!listChildren.isEmpty()){
            iterElem=listChildren.iterator();
            while(iterElem.hasNext()&&value==null){
                tmp=((Element)iterElem.next());
                value=tmp.getChild(elementName, namespace);
                if(value==null){
                    value=searchElement(tmp.getChildren(),elementName, namespace);
                    }
                 }
         }
        
         return value;
        
        
    }
   
   public Element getElementInStructure( String element,Namespace namespace ){
        
        Element value= rootElement.getChild(element, namespace);
        if(value==null){
            value=this.searchElement(rootElement.getChildren(), element, namespace);
     
        }
    
        return value;
  }
   
   
  public Element getElementByAttribute( String attributeId,String attributeValue,Namespace namespace){
   Element  elemento;
   List listaElementi;
  
   
   listaElementi=rootElement.getChildren();

   
   for(int i=0;i<listaElementi.size();i++){
       
        elemento=(Element)listaElementi.get(i);
        try{
            if(elemento.getAttributeValue(attributeId, namespace).equals(attributeValue)){
       
                return elemento;
            }
        }
        catch(NullPointerException ex){
        return null;
        }
   }
  return null;
  }
  
  
    /**
     * search recursuvely the element and returns the contents
     * @param root
     * @param element name of element
     * @param namespace
     * @return value of element, null if element don't exist
     */
    public static String getElementContentInStructure(Element root, String element,Namespace namespace ){
        
        String value= root.getChildText( element, namespace );
        Element result;
        if(value==null){
            result=ParserXML.searchElementStatic(root.getChildren(), element, namespace);
            if(result!=null){
                    value=result.getText();
            }
        }
    
        return value;
  }
 
    private static Element searchElementStatic(List<Element> listChildren,String elementName, Namespace namespace){
        
       Iterator iterElem;
        Element value=null;
        Element tmp;
            
        if(!listChildren.isEmpty()){
            
            iterElem=listChildren.iterator();
            while(iterElem.hasNext()&&value==null){
                
                tmp=((Element)iterElem.next());
                value=tmp.getChild(elementName, namespace);
              
                if(value==null){
                    value=searchElementStatic(tmp.getChildren(),elementName, namespace);
                    }
                 }
         }
        
         return value;
        
        
    }
   
   public static Element getElementInStructure(Element root, String element, Namespace namespace ){
        
        Element value= root.getChild(element, namespace);
        if(value==null){
            value=ParserXML.searchElementStatic(root.getChildren(), element, namespace);
            
        }
    
        return value;
  }
   
  public static Element getElementByAttribute(Element root, String attributeId,String attributeValue,Namespace namespace){
   Element elemento;
   List listaElementi;
  
   
   listaElementi=root.getChildren();

   
   for(int i=0;i<listaElementi.size();i++){
       
        elemento=(Element)listaElementi.get(i);
        try{
            if(elemento.getAttributeValue(attributeId, namespace).equals(attributeValue)){
       
                return elemento;
            }
        }
        catch(NullPointerException ex){
        return null;
        }
   }
  return null;
  }


}
