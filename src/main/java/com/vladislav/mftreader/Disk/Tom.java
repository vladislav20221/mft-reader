package com.vladislav.mftreader.Disk;

import com.vladislav.mftreader.ObjectAnalyzer;
import com.vladislav.mftreader.gpt.PartitionTableGPT;
import com.vladislav.mftreader.mft.TableMFT;
import com.vladislav.mftreader.ntfs.HeaderNTFS;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Абстрактное представлине тома диска.
 * @author Vladislav
 */
public class Tom {
    private final PartitionTableGPT partition;
    private final Disk disk;
    private final DataInputStream streamTom;
    
    private final HeaderNTFS headNTFS;
    
    // У каждого тома по две таблици MFT
    // Главная таблица MFT
    private TableMFT mainTableMFT;
    // Копия первых 4-х записей таблицы MFT
    private TableMFT copyTableMFT;    
    
    public Tom ( final Disk disk, final int n ) throws IOException {
        this.disk = disk;
        this.partition = new PartitionTableGPT(
                (DataInputStream) this.getDisk().getStreamDisk(), n );
        this.streamTom = disk.createStream();
        // Смещение потока на начало раздела
        this.streamTom.skipNBytes( getStartAddress() );
        // Считывание заголовка NTFS
        this.streamTom.mark( 120 );
        this.headNTFS = new HeaderNTFS( this.streamTom );
        
        if ( this.isEmpty() || this.partition.getNumber() == 0 ) {
            this.streamTom.close();
            return;
        }
        
        this.mainTableMFT = new TableMFT( this.disk, this, getMainMFTAddres(), getOffsetStartMFT(), true );
        this.copyTableMFT = new TableMFT( this.disk, this, getCopyMFTAddres(), getOffsetStartMFT(), false );
        
        this.streamTom.reset();
    }
    
    // Смещение пропускающее начальную запись MBR -> Заголовок GPT -> Списко томов GPT -> заголовок GPT.
    public long getOffsetStartMFT () {
        return this.headNTFS.getDosBase().getNumberSumSector().multiply( new BigInteger( "512" ) ).longValue();
    }
    public long getMainMFTAddres () {
        return this.headNTFS.getOneKlasterMFT().longValue();
    }
    public long getCopyMFTAddres () {
        return this.headNTFS.getCopyKlasterMFT().longValue();
    }
    public long getStartAddress () {
        return this.partition.getAddressStartLBA().longValue()*this.disk.getSizeLBA();
    }
    
    @Override
    public String toString () {
        return new ObjectAnalyzer().toString( this );
    }
    
    public final boolean isEmpty () {
        long labStart = this.getPartition().getAddressStartLBA().longValue();
        return labStart == 0;
    }

    
    public PartitionTableGPT getPartition() {return partition;}
    public Disk getDisk() {return disk;}
    public DataInputStream getStream() {return this.streamTom;}
    public HeaderNTFS getHeadNTFS() {return headNTFS;}
    public TableMFT getMainTableMFT() {return mainTableMFT;}
    public TableMFT getCopyTableMFT() {return copyTableMFT;}        
}