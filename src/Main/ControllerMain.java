package Main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ControllerMain
{
    @FXML
    private TextField pradinisSkaicius, galutinisSkaicius, didinimoIntervalas;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label procentuIndikatorius, skaidomasSkaicius, skaidymoPabaiga;

    private Thread thread;
    private boolean pirmas = true;

    private long buvesPradinis, buvesGalutinis, buvesIntervalas;

    private static PrintWriter writer;
    {
        try
        {
            writer = new PrintWriter("rezultatai.txt", "UTF-8");
        }
        catch (FileNotFoundException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Funkcija paspaudus mygtuką pradėti.
     */
    @FXML
    private void pradetiSkaiciuoti(ActionEvent event)
    {
        skaidymoPabaiga.setText("");
        try
        {
            long pradinis = Long.parseLong(pradinisSkaicius.getText());
            long galutinis = Long.parseLong(galutinisSkaicius.getText());
            long intervalas = Long.parseLong(didinimoIntervalas.getText());

            if (galutinis < pradinis)
            {
                Alert alert = new Alert(Alert.AlertType.NONE, "Galutinis skaičius turi būti mažesnis už pradinį", ButtonType.OK);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.OK) {
                    alert.close();
                }
            }
            else if (intervalas <= 0)
            {
                Alert alert = new Alert(Alert.AlertType.NONE, "Intervalas turi būti didesnis nei nulis", ButtonType.OK);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.OK) {
                    alert.close();
                }
            }
            else
                {
                if (!pirmas)
                {
                    thread.interrupt();
                }
                thread = new Thread(() ->
                {
                    try
                    {
                        skaiciuoti(pradinis, galutinis, intervalas);
                    }
                    catch (InterruptedException e)
                    {

                    }
                });
                thread.start();
            }
        }
        catch (NumberFormatException e)
        {
            Alert alert = new Alert(Alert.AlertType.NONE, "Neteisingai įvestas skaičius", ButtonType.OK);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }
        }

    }

    /**
     * Funkcija paspaudus mygtuką baigti.
     */
    @FXML
    private void baigtiSkaiciuoti(ActionEvent event)
    {
        thread.interrupt();
        spausdintiIFaila("Skaičiavimo buvo nutrauktas.");
        pirmas = true;
    }


    private void skaiciuoti(long pradinis, long galutinis, long intervalas) throws InterruptedException
    {
        if (buvesPradinis == pradinis && buvesGalutinis == galutinis && buvesIntervalas == intervalas)
        {
            if (pirmas)
            {
                spausdintiIFaila("Skaičiavimo pradžia. Naudojami skaičiai: " + pradinis + ", " + galutinis + ", " + intervalas + ".");
            }
            else
            {
                spausdintiIFaila("Skaičiavimo pradžia. Naudojami skaičiai: " + pradinis + ", " + galutinis + ", " + intervalas + ". -" +
                        "Šioje vietoje mygtukas pradėti buvo paspaustas dar kartą nepakeitus pradinių skaičių.");
            }
        }
        else
        {
            if (pirmas)
            {
                spausdintiIFaila("Skaičiavimo pradžia. Naudojami skaičiai: " + pradinis + ", " + galutinis + ", " + intervalas + ".");
            }
            else {
                spausdintiIFaila("Skaičiavimo pradžia. Naudojami skaičiai: " + pradinis + ", " + galutinis + ", " + intervalas + ". -" +
                        "Šioje vietoje mygtukas pradėti buvo paspaustas dar kartą su skirtingais pradiniais skaičiais.");
            }

        }

        pirmas = false;
        buvesPradinis = pradinis;
        buvesGalutinis = galutinis;
        buvesIntervalas = intervalas;

        progressBar.setProgress(0);

        ArrayList<Long> skaiciaiSkaidymui = new ArrayList<>();
        int skaidomuSkaiciuKiekis = (int) (((galutinis - pradinis) / intervalas) + 1);
        long esamasSkaicius = pradinis;
        float procentai = 0;
        float progresas = (float) 100/skaidomuSkaiciuKiekis;

        for (int i = 0; i < skaidomuSkaiciuKiekis; i++)
        {
            skaiciaiSkaidymui.add(esamasSkaicius);
            esamasSkaicius = esamasSkaicius + intervalas;
        }

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        for (int i = 0; i < skaidomuSkaiciuKiekis; i++)
        {
            dalinti(skaiciaiSkaidymui.get(i));
            procentai = procentai + progresas;

            float finalProcentai = procentai;
            int finalI = i;
            Platform.runLater(() ->
            {
                procentuIndikatorius.setText(df.format(finalProcentai) + "%");
                progressBar.setProgress(finalProcentai / 100);
                skaidomasSkaicius.setText("Skaidomas skaicius: " + skaiciaiSkaidymui.get(finalI));
            });
            Thread.sleep(500);
        }

        spausdintiIFaila("Skaičiavimo pabaiga.");
        Platform.runLater(() -> skaidymoPabaiga.setText("Skaidymas baigtas. Rezultatai faile rezultatai.txt"));

    }

    public static void spausdintiIFaila (String tekstas)
    {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss:SS ");
        String currentDate = dateFormat.format(date);
        writer.println(currentDate + tekstas + "\n");
    }

    private void dalinti(long skaicius)
    {
        if (skaicius == 0)
        {
            spausdintiIFaila(skaicius + " = " + skaicius);
        }
        else if (skaicius == 1)
        {
            spausdintiIFaila(skaicius + " = " + skaicius);
        }
        else
        {
            int daliklis;

            if (skaicius < 0) {
                daliklis = -1;
            } else
            {
                daliklis = 2;
            }

            String rezultatas = "";
            String dalinamasSkaicius = skaicius + " = ";

            while (skaicius != 1)
            {
                if (skaicius % daliklis == 0)
                {
                    skaicius = skaicius / daliklis;
                    rezultatas = rezultatas + "* " + daliklis + " ";
                    daliklis = 2;
                } else
                {
                    daliklis++;
                }
            }
            rezultatas = rezultatas.substring(2);
            rezultatas = dalinamasSkaicius + rezultatas;
            spausdintiIFaila(rezultatas);
        }
    }

    public static PrintWriter getWriter()
    {
        return writer;
    }
}
