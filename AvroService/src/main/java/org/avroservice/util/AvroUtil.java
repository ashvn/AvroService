package org.avroservice.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.commons.lang.StringUtils;
import org.avroservice.AvroConstants;
import org.avroservice.pojo.ColumnInfo;
import org.avroservice.pojo.TableMetaData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AvroUtil {
	
	public static GenericRecord convertAVSCToAvro(TableMetaData tableMetaData) throws JSONException {
		
		Schema schema = convertTableMetaDataToAVSC(tableMetaData);
		GenericRecord datum = new GenericData.Record(schema);
		return datum;
		
	}

	public static Schema convertMetaFileToAVSC(final String metaFileLocationWithName) throws FileNotFoundException, IOException, JSONException {
		
		TableMetaData tableMetaData=AvroFileUtil.parseMetaFile(metaFileLocationWithName);
		
		Schema tableSchema=convertTableMetaDataToAVSC(tableMetaData);
		
		return tableSchema;
	}
	
	public static Schema convertTableMetaDataToAVSC(final TableMetaData tableMetaData) throws JSONException {
		
		final JSONObject avscSchema=createJsonSchemaFromTableMetaData(tableMetaData);
		Schema avroSchema=new Schema.Parser().parse(avscSchema.toString());
		
		return avroSchema;
		
	}
	
	public static JSONObject createJsonSchemaFromTableMetaData(final TableMetaData tableMetaData) throws JSONException{
		
		JSONObject avscSchema=new JSONObject();
		
		avscSchema.put(AvroConstants.TYPE.getValue(), AvroConstants.TYPE_RECORD.getValue());
		avscSchema.put(AvroConstants.SOURCE_NAME.getValue(), tableMetaData.getSOURCE_NAME());
		avscSchema.put(AvroConstants.NAMESPACE.getValue(), tableMetaData.getSCHEMA_NAME());
		avscSchema.put(AvroConstants.TABLE_NAME.getValue(), tableMetaData.getTABLE_NAME());
		JSONArray fields=createFieldsFromTableMetaDataColumns(tableMetaData);
		avscSchema.put(AvroConstants.FIELDS.getValue(), fields);
		
		return avscSchema;
	}
	
	public static JSONArray createFieldsFromTableMetaDataColumns(final TableMetaData tableMetaData) throws JSONException
	{
		final Set<ColumnInfo> columns=tableMetaData.getColumns();
		JSONArray fields=new JSONArray();
		JSONObject field=null;
		for (ColumnInfo column : columns) {
			field=createFieldFromTableMetaDataColumn(column);
			fields.put(field);
		}
		return fields;
	}
	
	public static JSONObject createFieldFromTableMetaDataColumn(final ColumnInfo column) throws JSONException
	
	{
		JSONObject field =new JSONObject();
		field.put(AvroConstants.FIELD_NAME.getValue(), column.getColumnName());
		field.put(AvroConstants.FIELD_COLUMNNAME.getValue(), column.getColumnName());
		field.put(AvroConstants.FIELD_COLUMNID.getValue(), column.getColumnId());
		field.put(AvroConstants.FIELD_DATALENGTH.getValue(), column.getDataLength());
		field.put(AvroConstants.FIELD_COLUMNNAME.getValue(), column.getColumnName());
		field.put(AvroConstants.FIELD_DATASCALE.getValue(), column.getDataScale());
//		field.put(AvroConstants.FIELD_TYPE.getValue(), "[\"null\","+"\""+getType(column.getDataType()+"\""+"]"));
		field.put(AvroConstants.FIELD_TYPE.getValue(), getType(column.getDataType()));
		field.put(AvroConstants.FIELD_DATATYPE.getValue(), column.getDataType());
		field.put(AvroConstants.FIELD_FORMAT.getValue(), column.getFormat());
		return field;
	}
	
	public static String getType(final String type)
	{
		String returnType=null;
		
		if(type.equalsIgnoreCase("VARCHAR")){
			returnType="string";
		}else if(type.equalsIgnoreCase("DATE")){
			returnType="string";
		}else if(type.equalsIgnoreCase("TIMESTAMP")){
			returnType="string";
		}
		return returnType;
	}
	
	
	public static GenericRecord createGenericRecordForRow(final String input, final Schema oSchema, final boolean isFixedWidth, final String dataDelimiter)
	{
		GenericRecord record=null;
		if(isFixedWidth){
			record=createGenericRecordForFixedWidthRow(input, oSchema);
		}else{
			record=createGenericRecordForDelimitedRow(input, oSchema, dataDelimiter);
		}
		return record;
	}
	
	private static GenericRecord createGenericRecordForDelimitedRow(final String input, final Schema oSchema, final String dataDelimiter)
 {
		GenericRecord record = null;

		if (!StringUtils.isEmpty(input)) {
			// Creating Builder
			GenericRecordBuilder builder = new GenericRecordBuilder(oSchema);

			List<Field> fields = oSchema.getFields();

			Field currentField = null;

			final String[] tokens = StringUtils.splitPreserveAllTokens(input,
					dataDelimiter);
			for (int i = 0; i < fields.size(); i++) {
				currentField = fields.get(i);
				final int idx = currentField.pos();
//						.getProp(AvroConstants.FIELD_COLUMNID.getValue()); // Please
																			// check
//				if (!StringUtils.isEmpty(sIdx)) {
//					final int idx = Integer.parseInt(sIdx);
					builder.set(currentField, tokens[idx]);
//				}

			}
			record = builder.build();
		}
		return record;
	}
	private static GenericRecord createGenericRecordForFixedWidthRow(final String input, final Schema oSchema)
	{
		
		return null;
	}
}
