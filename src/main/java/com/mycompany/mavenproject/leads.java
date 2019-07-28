/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Ty McElroy
 */
public class leads 
{
    public static void main(String[] args) throws IOException 
    {
        
        // Set the state you want to search for.        
        String state = "Al";

        //set the counties you want to search for.
        String[] counties = {"Autauga"};

        //Set the departments you want to search for.
        String[] department = {"Police", "Fire", "EMS"};

        //This nested loop first gets the counties and then searches all the departments within those counties.
        for (String county : counties) 
        {
            for (int i = 0; i < department.length; i++) 
            {

                //set up webdriver so it can interact with Chrome
                System.setProperty("webdriver.chrome.driver", "C:\\Users\\Ty McElroy\\Desktop\\chromeDriver\\chromedriver.exe");

                //intantiate WebDriver and get the webpage for yellow pages so the rest of the code can interact with it.
                WebDriver driver = new ChromeDriver();
                driver.get("https://www.yellowpages.com/search?search_terms=huntsville+alabama+police+department&geo_location_terms=Deming%2C+NM");

                //get WebDriver to find the search box and click it. Then clear the box and input the data we want to search.
                driver.findElement(By.id("query")).click();
                driver.findElement(By.id("query")).clear();
                driver.findElement(By.id("query")).sendKeys(county + ", " + state + " " + department[i]);

                //click on and clear the location sseach box. Then input the location we want to search so we can extract the data from that page.
                driver.findElement(By.id("location")).click();
                driver.findElement(By.id("location")).clear();
                driver.findElement(By.id("location")).sendKeys(county + ", " + state);

                // find and click the search button so we can load the page with the results we want on it.
                WebElement textbox = driver.findElement(By.id("location"));
                textbox.sendKeys(Keys.ENTER);

                // we want to wait for the page to load so we can extract the appropraite data. Without this the first data to load
                // would be the data that is extracted. This is not what we want. We want the first result on the page.
                WebDriverWait wait = new WebDriverWait(driver, 20);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("query")));

                // get and connect the new URL to Jsoup so we can find and label the elements(data) on that page.
                String URL = driver.getCurrentUrl();
                Document doc = Jsoup.connect(URL).ignoreHttpErrors(true).get();

                //These are the variables that we will print to the Word document
                String strTitle = "";
                String strPhone = "";
                String strStreet = "";
                String strLocality = "";
                String href = "";

                //When we search, sometimes no results will pop up. If this happens then we will set all variables to "N/A".
                if (driver.findElements(By.id("no-results")).size() > 0) 
                {
                    strTitle = "N/A";
                    strPhone = "N/A";
                    strStreet = "N/A";
                    strLocality = "N/A";
                    href = "N/A";

                    
                    //Connect path to word document so we can print to it.
                    Path productsPath = Paths.get("C:\\Users\\Ty McElroy\\Desktop\\JavaStuff\\JavaIO");
                    File productsFile = productsPath.toFile();

                    try (PrintWriter out = new PrintWriter(
                            new BufferedWriter(
                                    new FileWriter(productsFile, true)))) 
                    {
                        //We need an orginal line so the rest of the data can be appeneded. Without this the first data is formatted weird.
                        // This line also allows for us to separate and see our data clearly.
                        out.println("-----------------------------------------------------------");
                        out.println();

                        out.println(county + " " + department[i] + "= " + strTitle);
                        out.println(county + department[i] + "= " + strPhone);
                        out.println(county + department[i] + "= " + strStreet + " " + strLocality);
                        out.println(county + department[i] + "= " + href);
                        out.println();

                        out.flush();
                        out.close();

                        driver.close();
                        
                    } 
                    catch (Exception e) 
                    {
                        System.out.println(e);
                    }
                } 
                
                // find the first result out of the list of results if the page does return results.
                else 
                {
                    //find and label the elements(data) on the page
                    if (!doc.getElementsByClass("business-name").get(0).text().isEmpty()) 
                    {
                        strTitle = doc.getElementsByClass("business-name").get(0).text();
                    } 
                    else 
                    {
                        strTitle = "N/A";
                    }
                    if (!doc.getElementsByClass("phones phone primary").get(0).text().isEmpty()) 
                    {
                        strPhone = doc.getElementsByClass("phones phone primary").get(0).text();
                    } else 
                    {
                        strPhone = "N/A";
                    }
                    if (!doc.getElementsByClass("street-address").get(0).text().isEmpty()) 
                    {
                        strStreet = doc.getElementsByClass("street-address").get(0).text();
                    } 
                    else 
                    {
                        strStreet = "N/A";
                    }
                    if (!doc.getElementsByClass("locality").get(0).text().isEmpty()) 
                    {
                        strLocality = doc.getElementsByClass("locality").get(0).text();
                    } 
                    else 
                    {
                        strLocality = "N/A";
                    }
                    if (!driver.findElements(By.linkText("Website")).get(0).getAttribute("href").isEmpty()) 
                    {
                        href = driver.findElements(By.linkText("Website")).get(0).getAttribute("href");
                    } else 
                    {
                        href = "N/A";
                    }

                    //Connect path to word document so we can print to it.
                    Path productsPath = Paths.get("C:\\Users\\Ty McElroy\\Desktop\\JavaStuff\\JavaIO");
                    File productsFile = productsPath.toFile();

                    try (PrintWriter out = new PrintWriter(
                            new BufferedWriter(
                                    new FileWriter(productsFile, true)))) 
                    {
                        //We need an orginal line so the rest of the data can be appeneded. Without this the first data is formatted weird.
                        // This line also allows for us to separate and see our data clearly.
                        out.println("-----------------------------------------------------------");
                        out.println();

                        out.println(strTitle);
                        out.println(strPhone);
                        out.println(strStreet + strLocality);
                        out.println(href);
                        out.println();

                        out.flush();
                        out.close();

                        driver.close();
                    } 
                    catch (Exception e) 
                    {
                        System.out.println(e);
                    }

                }

            }
        }
    }
}
