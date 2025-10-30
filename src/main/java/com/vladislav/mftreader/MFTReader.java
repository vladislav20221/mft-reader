package com.vladislav.mftreader;

import com.vladislav.mftreader.Disk.Disk;
import com.vladislav.mftreader.Disk.Tom;
import com.vladislav.mftreader.mft.record.RecordMFT;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vladislav
 */
public class MFTReader {
    private static final String FILE_NAME = "disk_Download.raw";
    private static final Path DISK = Paths.get( "I:\\", FILE_NAME );
    
    
    public static void main( String[] args ) throws InterruptedException {
        try {
            pringFileImageInfo( DISK );
            //final InputStream stream = new FileInputStream( DISK.toFile() );
            //final DataInputStream streamTom = new DataInputStream( stream );
            //Thread.sleep( 10_000 );
            
            final Disk disk_0 = new Disk ( DISK );
            System.out.println( disk_0.getPrimaryGPT() );
            disk_0.getTomGPT().forEach(System.out::println);
            final Tom tom = disk_0.getTomGPT().get(1);
            System.out.println( tom.getHeadNTFS() );
            List<RecordMFT> recordMFT = tom.getMainTableMFT().readMft( 8000 );
            recordMFT.stream().map((e)->e.getAttributeList()).forEach((a)->a.forEach(System.out::println));
            
            //recordMFT.forEach( System.out::println );
            //final String format = "%d %s %s %s %s %s";
            //for ( RecordMFT record: recordMFT ) {
            //    System.out.println( format.formatted( record.getHeaderMFT() ) );
            //}
            
            
            
            // Прочитывание наперёд...
            //binnaryHexPrint( disk_0.getListPartitionTableGPT().get( 1 ).getMainTableMFT().getStreamMFT() , 16, 100 );
            // 16777216
            //streamTom.skip( 16777216 );
            //binnaryHexPrint( streamTom, 16, 50 );
            
            disk_0.getStreamDisk().close();
        } catch ( FileNotFoundException ex ) {
            Logger.getLogger(MFTReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch ( IOException ex ) {
            Logger.getLogger(MFTReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void pringFileImageInfo ( final Path path ) {
        final File file = path.toFile();
        final float size = ((float)file.length())/(float)(1<<30);
        System.out.println( String.format( "Имя: %s Размер: %.3f Гб", file.getName(), size ) );
    }
    
    private static void binnaryHexPrint ( final InputStream data, final int n, final int line ) 
            throws IOException {
        final StringBuilder build = new StringBuilder();
        final String format = "%3s";
        for ( int i = 0; i<line; i++ ) {
            for ( int j = 0; j<n; j++ ) {
                int b = data.read()&0xFF;
                build.append( format.formatted( Integer.toHexString( b ) ) );
                build.append( format.formatted( (char) b ) );
            }
            build.append( "\n" );
        }
        System.out.println( build.toString() );
    }
    
    private static void readOneReader ( final InputStream data ) throws IOException {
        int b;
        int z = 0;
        int z2 = 0;
        while ( (b = data.read()) != -1 ) {
            z++;                
            if ( b != 0 ) {
                z2++;
                System.out.println( "Прочитано: "+z );
            }
            if ( z2 >= 560 ) break;
        }
    }
}