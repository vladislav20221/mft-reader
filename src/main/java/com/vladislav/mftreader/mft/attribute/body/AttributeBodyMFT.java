package com.vladislav.mftreader.mft.attribute.body;

import com.vladislav.mftreader.mft.attribute.RecordResidentAtrtribute;
import com.vladislav.mftreader.mft.attribute.head.AttributeHeadMFT;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.logging.Logger;

/**
 * Абстрактное представление тела атрибута.
 * @author Vladislav
 */
public class AttributeBodyMFT {
    private static final Logger LOG = Logger.getLogger( AttributeBodyMFT.class.getName() );
    // Резедентный заголовок. На данный момент реализовано только чтение резидентных заголовков.
    private final RecordResidentAtrtribute residentAttribute;
    
    private final AttributeBody_Standart_Information standartInfo;
    private final AttributeBody_FileName full_name;
    
    public AttributeBody_FileName getAttributeFileName () {
        return this.full_name;
    }
    public AttributeBody_Standart_Information getAttributeStandartInforamtion () {
        return this.standartInfo;
    }
    
    public AttributeBodyMFT ( final DataInputStream data, 
                              final AttributeHeadMFT head ) throws IOException {
        // Заполнение тела атрибута
        boolean flag = false;
        if ( !head.isRezident() ) {
            this.residentAttribute = new RecordResidentAtrtribute( data, head.getLengthName().intValue() );
        } else {
            this.residentAttribute = null;
        }
        // Считывание конктретных атрибутов        
        if ( head.getAttributeType().equals( "$STANDARD_INFORMATION" ) ) {
            standartInfo = new AttributeBody_Standart_Information ( data );
            flag = true;
        } else  {
            standartInfo = null;
        }
        if ( head.getAttributeType().equals( "$FULL_FILE_NAME" ) ) {
            full_name = new AttributeBody_FileName( data );
            flag = true;
        } else {
            full_name = null;
        }
        if ( !flag ) {
            // Был прочитан атрибут чтение которого пока что не реализовано
            LOG.warning( "Чтение атрибута <%s> на данный момент не реализовано.".formatted( head.getAttributeType() ) );
            //System.out.println( this.residentAttribute );
            //System.out.println( "-=-=-=-=-=-\t"+this.residentAttribute.getLengthBody().longValue() );
            //data.skipNBytes( this.residentAttribute.getLengthBody().longValue() );
        }
        //if ( AttributeHeadMFT.kodeTypeAttributeMap.getOrDefault( head.getType().intValue(), "" ).equals( "$DATA" ) ) {
            // Считывание неризидентный атрибутов пока что не реализовано необходимо проуситть данную запись
        //    data.skipBytes( head.getSize().intValue()-AttributeHeadMFT.SIZE_HEAD_ATTRIBUTE_MFT_BASE );            
        //}
        //else {
            // В конце может возникнуть ситуация, когда до конца запии тела атрибута остаются пустые байты.
            // Если из размера записи из базовой записи атрибута 
            // Отнять = смещение до тела + размер записи тела. То можно будет найти не используемые байты которые и будут пропущены.
            if ( this.residentAttribute != null ) {
                BigInteger sum = this.residentAttribute.getLengthBody().add( this.residentAttribute.getOffsetBody() );
                BigInteger value = head.getSize().subtract( sum );
                //System.out.println( "Длина тела атрибута: "+this.residentAttribute.getLengthBody() );
                //System.out.println( "Длина заголовка атрибута: "+this.residentAttribute.getOffsetBody() );                
                //System.out.println( "Общая длина атрибута: "+sum );
                //System.out.println( "Область памяти занимаемая атрибутом: "+head.getSize() );
                //System.out.println( "разница: "+value );
                head.getSize().add( value );
                if ( value.intValue() != 0 )
                    data.skipBytes( value.intValue() );
            }
        //}
    }

    @Override
    public String toString () {
        StringBuilder build = new StringBuilder();
        if ( this.standartInfo != null )
            build.append( this.standartInfo.toString() );
        if ( this.full_name != null )
            build.append( this.full_name );
        return build.toString();
    }
    
    public RecordResidentAtrtribute getResidentAttribute() {return residentAttribute;}
    public AttributeBody_Standart_Information getStandartInfo() {return standartInfo;}
    public AttributeBody_FileName getFull_name() {return full_name;}
}