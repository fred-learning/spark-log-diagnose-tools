package bin;

import parseSourceCode.GetSourceSchema;

/**
 * Created by Administrator on 2015/12/17.
 */
public class ExtractDict {
    public static void main(String[] args) throws Exception {
        String codepath = "D:/sparkforfilterlogline";
        String orioutput = "E:/oriSchemaList";
        String regularoutput = "E:/regularSchemaList";
        GetSourceSchema.getSchema(codepath, orioutput, regularoutput);
    }
}
