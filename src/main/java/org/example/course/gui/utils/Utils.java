package org.example.course.gui.utils;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils
{
    public static Stage currentStage(ActionEvent event)
    {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    public static Integer tryParseToInt(String string)
    {
        try
        {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    public static Double tryParseToDouble(String string)
    {
        try
        {
            return Double.parseDouble(string);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    public static <T> void formatTableColumnDate(TableColumn<T, LocalDate> tableColumn, String format)
    {
        tableColumn.setCellFactory(column ->
        {
            TableCell<T, LocalDate> cell = new TableCell<>()
            {
                private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);

                @Override
                protected void updateItem(LocalDate item, boolean empty)
                {
                    super.updateItem(item, empty);
                    if (empty)
                    {
                        setText(null);
                    } else
                    {
                        setText(dateTimeFormatter.format(item));
                    }
                }
            };
            return cell;
        });
    }

    public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, int decimalPlaces)
    {
        tableColumn.setCellFactory(column ->
        {
            TableCell<T, Double> cell = new TableCell<>()
            {
                @Override
                protected void updateItem(Double item, boolean empty)
                {
                    super.updateItem(item, empty);
                    if (empty)
                    {
                        setText(null);
                    }
                    else
                    {
                        Locale.setDefault(Locale.US);
                        setText(String.format("%." + decimalPlaces + "f", item));
                    }
                }
            };
            return cell;
        });
    }

    public static void formatDatePicker(DatePicker datePicker, String format)
    {
        datePicker.setConverter(new StringConverter<LocalDate>()
        {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
            {
                datePicker.setPromptText(format.toLowerCase());
            }
            @Override
            public String toString(LocalDate date)
            {
                if (date != null)
                {
                    return dateTimeFormatter.format(date);
                }
                else
                {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string)
            {
                if (string != null && !string.isEmpty())
                {
                    return LocalDate.parse(string, dateTimeFormatter);
                }
                else
                {
                    return null;
                }
            }
        });
    }
}
