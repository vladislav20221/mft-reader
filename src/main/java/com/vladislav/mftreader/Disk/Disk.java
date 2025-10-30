package com.vladislav.mftreader.Disk;

import com.vladislav.mftreader.mbr.DescriptSection;
import com.vladislav.mftreader.gpt.PrimaryGPT;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Абстрактное представление диска.
 * @author Vladislav
 */
public class Disk {
    // ЛОгер для данного класса
    private static final Logger LOG = Logger.getLogger( Disk.class.getName() );
    // Типы разметок диска: GPT либо MBR.
    private enum DiskLayout { GPT, MBR };
    
    // Базовая информация о диске
    // Размер сектора (байт).
    private int sizeSector = 512;
    // Размер кластера (Секторов).
    private int sizeCluster = 8;
    // Размер записи LBA-N (байт).
    private int sizeLBA = 512;
    // Уникальная сигнатура диска
    private final byte[] signature = new byte[4];
    
    private int byfferSize = 4096*8;    
    private final Path DISK;
    
    // Первичный GPT заголовок
    private PrimaryGPT primaryGPT;
    
    // Список томов диска
    private final List<Tom> listPartitionTableGPT = new ArrayList( 128 );
    // Записи описывающие тома диска из MBR
    private final List<DescriptSection> listTomDescriptionMBR = new ArrayList( 4 );
    
    
    
    // Поток ввода связанный с этим диском.
    private final InputStream streamDisk;
    
    public Disk ( final Path path ) throws FileNotFoundException, IOException {
        this.DISK = path;        
        this.streamDisk = createStream();
        
        readPartitionTbale();
        readPrimaryGPT();
        readPartitionTableGPT();
        // После считывания основной части
        
    }
    public Disk ( final Path path, final int byfferSize ) throws FileNotFoundException, IOException {
        this.DISK = path;
        this.byfferSize = byfferSize;        
        this.streamDisk = createStream();
        
        readPartitionTbale();
        readPrimaryGPT();
        readPartitionTableGPT();
        // После считывания основной части
        
    }
    
    private void readPartitionTableGPT () throws IOException {
        // Далее идёт таблциа раздело как правило максимум из 128 записей
        for ( int i = 0; i< 128; i++ ) {
            Tom tom = new Tom ( this, i );
            System.out.println( tom.toString() );
            if ( !tom.isEmpty() ) {
                this.listPartitionTableGPT.add( tom );
            }
        }
        LOG.info( "Обнаружено <%d> томов на диске.".formatted( this.listPartitionTableGPT.size() ) );
    }
    /**
     * Считывания первичного заголовка GPT
     * @throws IOException 
     */
    private void readPrimaryGPT () throws IOException {
        this.primaryGPT = new PrimaryGPT ( (DataInputStream) this.getStreamDisk() );
    }
    /**
     * Считывание заиси MBR.
     * @throws IOException 
     */
    private void readPartitionTbale () throws IOException {
        // Пропускаем исполняемый код MBR
        this.getStreamDisk().skip( 440 );
        // Далее может идти уникальная сигнатура диска        
        this.getStreamDisk().read(this.getSignature());
        // Следующие 2 байта не используются
        this.getStreamDisk().skip( 2 );
        // Данный маркер позволяет считать загрузочную запись.
        // И все записи GPT связанные с разделами диска.
        this.getStreamDisk().mark( 512*2+128*128 );
        // После 446 байт идут 4 заиси
        // При разметке MBR каждому физическому разделу
        // Соответствует одна такая запись. Но вазможно их увиличиние за счёт логическхи разделов
        // При разметке GPT действительна лишь одна запись - первая. Осталдьные равны нулю. 
        // В конце записей описания разделов стоит сигнатура [55AA]
        
        // Считывание защитной запси MBR
        DescriptSection des = new DescriptSection( (DataInputStream) this.getStreamDisk(), 0 );
        this.getListTomDescriptionMBR().add( des );
        // Пропускаем пустые записи и так же сигнатуру 55AA
        this.getStreamDisk().skip( 16*3+2 );
    }
    
    public final DataInputStream createStream () throws FileNotFoundException, IOException {
        InputStream stream = new FileInputStream( this.getDISK().toFile() );
        BufferedInputStream buffer = new BufferedInputStream( stream, this.getByfferSize());
        DataInputStream data = new DataInputStream( buffer );        
        return data;
    }
    
    public final DataInputStream getStreamTom ( final int index ) {
        return this.listPartitionTableGPT.get(index).getStream();
    }
    public List<DescriptSection> getTomMBR () {
        return this.listTomDescriptionMBR;
    }
    public List<Tom> getTomGPT () {
        return this.listPartitionTableGPT;
    }
    
    public int getSizeSector() {return sizeSector;}
    public int getSizeCluster() {return sizeCluster;}
    public int getSizeLBA() {return sizeLBA;}
    public byte[] getSignature() {return signature;}
    public int getByfferSize() {return byfferSize;}
    public Path getDISK() {return DISK;}
    public PrimaryGPT getPrimaryGPT() {return primaryGPT;}
    public List<Tom> getListPartitionTableGPT() {return listPartitionTableGPT;}
    public List<DescriptSection> getListTomDescriptionMBR() {return listTomDescriptionMBR;}
    public InputStream getStreamDisk() {return streamDisk;}        
}