package com.vladislav.mftreader.mft.attribute;

import com.vladislav.mftreader.mft.TableMFT;
import com.vladislav.mftreader.mft.attribute.body.AttributeBodyMFT;
import com.vladislav.mftreader.mft.attribute.head.AttributeHeadMFT;
import com.vladislav.mftreader.mft.record.RecordHeaderMFT;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Абстрактное представлине атрибута MFT.
 * Люой атрибут MFT сотоит из заголовка двх типов и тела.
 * Длина атрибута может быть разной. 
 * @author Vladislav
 */
public class AttributeMFT {
    private static final Logger LOG = Logger.getLogger( AttributeMFT.class.getName() );
    
    // Область памяти занимаемая атрибутом 
    private final AttributeHeadMFT head;
    private final AttributeBodyMFT body;
    
    private final RecordHeaderMFT headMFT;
    
    public AttributeMFT ( final DataInputStream data, final long finish, final RecordHeaderMFT headMFT ) throws IOException, EOFException {
        this.headMFT = headMFT;
        this.head = new AttributeHeadMFT( data );
        
        if ( this.head.isRezident() ) {
            this.body = null;
            long offset = this.head.getSize().longValue()-AttributeHeadMFT.SIZE_HEAD_ATTRIBUTE_MFT_BASE;
            //System.out.println( "Занимает памяти "+this.head.getSize().longValue() );
            data.skipNBytes( offset );
        } else {
            if ( this.head.getAttributeType().equals( "$STANDARD_INFORMATION" ) ||
                 this.head.getAttributeType().equals( "$FULL_FILE_NAME" ) ) {
                this.body = new AttributeBodyMFT( data, this.head );
            } else {                
                long offset = this.head.getSizeBody().intValue();
                //System.out.println( "Длина атрибута вместе с заголовком: "+this.head.getSize() );
                //System.out.println( "Длина атрибута без заголовка: "+this.head.getSizeBody() );
                //System.out.println( this.head );
                LOG.info( "Найден атрибут с неизвестной формой записи %s.".formatted( this.head.getAttributeType() ) );
                if ( offset > finish ) {
                    data.skipNBytes( finish );
                    //this.head.gets
                } else {
                    data.skipNBytes( offset );
                }
                this.body = null;
            }
        }
    }
    
    @Override
    public String toString () {
        if ( body == null ) return "";
        StringBuilder build = new StringBuilder();
        build.append( "Индекс файловой записи: " ).append( this.headMFT.getIndexRecordMFT() ).append( "\n" );
        //build.append( "Флаг упаковки атрибута: " ).append( this.head.getPacked() ).append( "\n" );        
        if ( this.body.getFull_name() != null ) {
            build.append( "Имя файла: " ).append( this.body.getFull_name().getFileName() ).append( "\n" );
            build.append( "Размер файла: ").append( this.body.getAttributeFileName().getRealSize() ).append("\n");
            //build.append( "Флаги доступа DOS: " ).append( this.body.getFull_name().getPermissions() ).append( "\n" );
            build.append( this.body.getFull_name().getTimeStamp() ).append( "\n" );
        }
        if ( this.body.getStandartInfo() != null ) {
            //build.append( "" ).append( this.body.getStandartInfo().get ).append( "\n" );
            //build.append( "" ).append( "" ).append( "\n" );
            //build.append( "" ).append( "" ).append( "\n" );
        }        
        //build.append( "" ).append( "" ).append( "\n" );
        //build.append( "" ).append( "" ).append( "\n" );
        return build.toString();
    }
    
    public AttributeHeadMFT getHead() {return head;}
    public AttributeBodyMFT getBody() {return body;}
}