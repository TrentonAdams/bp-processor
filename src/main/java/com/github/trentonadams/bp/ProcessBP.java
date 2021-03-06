package com.github.trentonadams.bp;

import org.jsefa.Deserializer;
import org.jsefa.Serializer;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.xml.XmlIOFactory;
import org.jsefa.xml.XmlSerializer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Class for processing withings health mate data obtained from the wireless
 * blood pressure monitor.
 * <p/>
 * First argument is CSV to read, assuming no headers at top of file
 * <p/>
 * Second argument is CSV file to create/overwrite with compiled daily average
 * data.
 * <p/>
 * We assume you are using comments the same way we do, as comma separated
 * tags.
 * <p/>
 * Created :  25/05/14 4:30 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
public class ProcessBP
{

    private static final String[] STRINGS = new String[]{};

    public static void main(final String[] args)
        throws IOException, CloneNotSupportedException
    {

        final CsvConfiguration config = new CsvConfiguration();
        config.setFieldDelimiter(',');

        // Setup for reading original CSV
        final CsvIOFactory deserializerFactory = CsvIOFactory.createFactory(
            config,
            BPLine.class);
        final Deserializer deserializer =
            deserializerFactory.createDeserializer();

        final FileReader fileReader = new FileReader(args[0]);
        deserializer.open(fileReader);

        // Setup for writing new averaged CSV
        final CsvIOFactory serializerFactory = CsvIOFactory.createFactory(
            config, BPLine.class);
        final Serializer serializer = serializerFactory.createSerializer();
        final FileWriter fileWriter = new FileWriter(args[1]);
        serializer.open(fileWriter);

        // setup averaging storage
        final List<BPLine> currentLines = new ArrayList<BPLine>();
        final Set<String> tags = new TreeSet<String>();

        BPLine previousLine = null;
        // cycling through the rows
        while (deserializer.hasNext())
        {
            final BPLine currentBPLine = deserializer.next();
            currentLines.add(currentBPLine);
            final String sTags = currentBPLine.getTags();
            final String[] curTags =
                sTags == null ? STRINGS : sTags.split(",");
            for (final String tag : curTags)
            {   // compiling complete list of tags, and merging them.
                tags.add(tag.trim());
            }

            final Calendar cal = currentBPLine.getCalendar();
            // if previousCal not yet set, return current cal, as it is then equal
            final Calendar previousCal =
                previousLine != null ? previousLine.getCalendar() : cal;
            if (cal.get(Calendar.DAY_OF_YEAR) != previousCal.get(
                Calendar.DAY_OF_YEAR))
            {   // changing dates, do the average and combine the tags
                calcAverage(serializer, currentLines, tags, previousLine);
            }
            previousLine = currentBPLine;
        }
        calcAverage(serializer, currentLines, tags, previousLine);

        serializer.close(true);
        deserializer.close(true);
    }

    private static void calcAverage(Serializer serializer,
        List<BPLine> currentLines, Set<String> tags, BPLine previousLine)
    {
        int hrTotal = 0;
        int systolicTotal = 0;
        int diastolicTotal = 0;
        for (final BPLine line : currentLines)
        {
            hrTotal += line.getHr();
            systolicTotal += line.getSystolic();
            diastolicTotal += line.getDiastolic();
        }

        // average the readings
        hrTotal = hrTotal / currentLines.size();
        systolicTotal = systolicTotal / currentLines.size();
        diastolicTotal = diastolicTotal / currentLines.size();
        previousLine.setHr(hrTotal);
        previousLine.setSystolic(systolicTotal);
        previousLine.setDiastolic(diastolicTotal);

        // separate tags by commas...
        String csvTags = "";
        final String[] array = tags.toArray(STRINGS);
        for (int i = 0; i < array.length; i++)
        {
            final String tag = array[i];
            csvTags += tag;
            if (i < array.length - 1)
            {
                csvTags += ',';
            }
        }
        previousLine.setTags(csvTags);

        // output data to new csv file
        serializer.write(previousLine);
        currentLines.clear();
        tags.clear();
    }
}
