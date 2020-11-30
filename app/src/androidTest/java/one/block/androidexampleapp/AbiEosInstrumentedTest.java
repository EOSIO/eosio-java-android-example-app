package one.block.androidexampleapp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.runner.AndroidJUnit4;
import one.block.eosiojava.error.serializationProvider.DeserializeAbiError;
import one.block.eosiojava.error.serializationProvider.DeserializeError;
import one.block.eosiojava.error.serializationProvider.DeserializeTransactionError;
import one.block.eosiojava.error.serializationProvider.SerializationProviderError;
import one.block.eosiojava.error.serializationProvider.SerializeTransactionError;
import one.block.eosiojava.models.AbiEosSerializationObject;
import one.block.eosiojavaabieosserializationprovider.AbiEosSerializationProviderImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

// At this point its not possible to run these as normal unit tests even though the context
// access to pick up the abi json as resources has been removed.  This is because we are
// using the NDK to compile the abieos c++ code, which results in shared libraries that need
// android to run.

@RunWith(AndroidJUnit4.class)
public class AbiEosInstrumentedTest {

    private static AbiEosSerializationProviderImpl abieos;

    @BeforeClass
    public static void startSetup() {
        try {
            abieos = new AbiEosSerializationProviderImpl();
        } catch (SerializationProviderError serializationProviderError) {
            serializationProviderError.printStackTrace();
            fail();
        }
    }

    @AfterClass
    public static void endTearDown() {
        abieos.destroyContext();
        abieos = null;
    }

    @Test
    public void hexToJsonAbiDef() {
        String hex = "0E656F73696F3A3A6162692F312E30010C6163636F756E745F6E616D65046E616D6505087472616E7366657200040466726F6D0C6163636F756E745F6E616D6502746F0C6163636F756E745F6E616D65087175616E74697479056173736574046D656D6F06737472696E67066372656174650002066973737565720C6163636F756E745F6E616D650E6D6178696D756D5F737570706C79056173736574056973737565000302746F0C6163636F756E745F6E616D65087175616E74697479056173736574046D656D6F06737472696E67076163636F756E7400010762616C616E63650561737365740E63757272656E63795F7374617473000306737570706C790561737365740A6D61785F737570706C79056173736574066973737565720C6163636F756E745F6E616D6503000000572D3CCDCD087472616E73666572BC072D2D2D0A7469746C653A20546F6B656E205472616E736665720A73756D6D6172793A205472616E7366657220746F6B656E732066726F6D206F6E65206163636F756E7420746F20616E6F746865722E0A69636F6E3A2068747470733A2F2F63646E2E746573746E65742E6465762E62316F70732E6E65742F746F6B656E2D7472616E736665722E706E6723636535316566396639656563613334333465383535303765306564343965373666666631323635343232626465643032353566333139366561353963386230630A2D2D2D0A0A2323205472616E73666572205465726D73202620436F6E646974696F6E730A0A492C207B7B66726F6D7D7D2C20636572746966792074686520666F6C6C6F77696E6720746F206265207472756520746F207468652062657374206F66206D79206B6E6F776C656467653A0A0A312E204920636572746966792074686174207B7B7175616E746974797D7D206973206E6F74207468652070726F6365656473206F66206672617564756C656E74206F722076696F6C656E7420616374697669746965732E0A322E2049206365727469667920746861742C20746F207468652062657374206F66206D79206B6E6F776C656467652C207B7B746F7D7D206973206E6F7420737570706F7274696E6720696E6974696174696F6E206F662076696F6C656E636520616761696E7374206F74686572732E0A332E2049206861766520646973636C6F73656420616E7920636F6E747261637475616C207465726D73202620636F6E646974696F6E732077697468207265737065637420746F207B7B7175616E746974797D7D20746F207B7B746F7D7D2E0A0A4920756E6465727374616E6420746861742066756E6473207472616E736665727320617265206E6F742072657665727369626C6520616674657220746865207B7B247472616E73616374696F6E2E64656C61795F7365637D7D207365636F6E6473206F72206F746865722064656C617920617320636F6E66696775726564206279207B7B66726F6D7D7D2773207065726D697373696F6E732E0A0A4966207468697320616374696F6E206661696C7320746F20626520697272657665727369626C7920636F6E6669726D656420616674657220726563656976696E6720676F6F6473206F722073657276696365732066726F6D20277B7B746F7D7D272C204920616772656520746F206569746865722072657475726E2074686520676F6F6473206F72207365727669636573206F7220726573656E64207B7B7175616E746974797D7D20696E20612074696D656C79206D616E6E65722E0000000000A531760569737375650000000000A86CD445066372656174650002000000384F4D113203693634010863757272656E6379010675696E743634076163636F756E740000000000904DC603693634010863757272656E6379010675696E7436340E63757272656E63795F737461747300000000";
        String jsonResult = "{\"version\":\"eosio::abi/1.0\",\"types\":[{\"new_type_name\":\"account_name\",\"type\":\"name\"}],\"structs\":[{\"name\":\"transfer\",\"base\":\"\",\"fields\":[{\"name\":\"from\",\"type\":\"account_name\"},{\"name\":\"to\",\"type\":\"account_name\"},{\"name\":\"quantity\",\"type\":\"asset\"},{\"name\":\"memo\",\"type\":\"string\"}]},{\"name\":\"create\",\"base\":\"\",\"fields\":[{\"name\":\"issuer\",\"type\":\"account_name\"},{\"name\":\"maximum_supply\",\"type\":\"asset\"}]},{\"name\":\"issue\",\"base\":\"\",\"fields\":[{\"name\":\"to\",\"type\":\"account_name\"},{\"name\":\"quantity\",\"type\":\"asset\"},{\"name\":\"memo\",\"type\":\"string\"}]},{\"name\":\"account\",\"base\":\"\",\"fields\":[{\"name\":\"balance\",\"type\":\"asset\"}]},{\"name\":\"currency_stats\",\"base\":\"\",\"fields\":[{\"name\":\"supply\",\"type\":\"asset\"},{\"name\":\"max_supply\",\"type\":\"asset\"},{\"name\":\"issuer\",\"type\":\"account_name\"}]}],\"actions\":[{\"name\":\"transfer\",\"type\":\"transfer\",\"ricardian_contract\":\"---\\u000Atitle: Token Transfer\\u000Asummary: Transfer tokens from one account to another.\\u000Aicon: https://cdn.testnet.dev.b1ops.net/token-transfer.png#ce51ef9f9eeca3434e85507e0ed49e76fff1265422bded0255f3196ea59c8b0c\\u000A---\\u000A\\u000A## Transfer Terms & Conditions\\u000A\\u000AI, {{from}}, certify the following to be true to the best of my knowledge:\\u000A\\u000A1. I certify that {{quantity}} is not the proceeds of fraudulent or violent activities.\\u000A2. I certify that, to the best of my knowledge, {{to}} is not supporting initiation of violence against others.\\u000A3. I have disclosed any contractual terms & conditions with respect to {{quantity}} to {{to}}.\\u000A\\u000AI understand that funds transfers are not reversible after the {{$transaction.delay_sec}} seconds or other delay as configured by {{from}}'s permissions.\\u000A\\u000AIf this action fails to be irreversibly confirmed after receiving goods or services from '{{to}}', I agree to either return the goods or services or resend {{quantity}} in a timely manner.\"},{\"name\":\"issue\",\"type\":\"issue\",\"ricardian_contract\":\"\"},{\"name\":\"create\",\"type\":\"create\",\"ricardian_contract\":\"\"}],\"tables\":[{\"name\":\"accounts\",\"index_type\":\"i64\",\"key_names\":[\"currency\"],\"key_types\":[\"uint64\"],\"type\":\"account\"},{\"name\":\"stat\",\"index_type\":\"i64\",\"key_names\":[\"currency\"],\"key_types\":[\"uint64\"],\"type\":\"currency_stats\"}],\"ricardian_clauses\":[],\"error_messages\":[],\"variants\":[],\"action_results\":[],\"kv_tables\":{}}";

        String json = null;

        try {
            json = abieos.deserializeAbi(hex);
        } catch (DeserializeAbiError err) {
            err.printStackTrace();
        }

        assertNotNull(json);
        assertEquals(json, jsonResult);

    }

    @Test
    public void hexToJsonAbiTransaction() {
        String hex = "AE0D635CDCAC90A6DCFA000000000100A6823403EA3055000000572D3CCDCD0100AEAA4AC15CFD4500000000A8ED32323B00AEAA4AC15CFD4500000060D234CD3DA06806000000000004454F53000000001A746865206772617373686F70706572206C69657320686561767900";
        String jsonResult = "{\"expiration\":\"2019-02-12T18:17:18.000\",\"ref_block_num\":44252,\"ref_block_prefix\":4208764560,\"max_net_usage_words\":0,\"max_cpu_usage_ms\":0,\"delay_sec\":0,\"context_free_actions\":[],\"actions\":[{\"account\":\"eosio.token\",\"name\":\"transfer\",\"authorization\":[{\"actor\":\"cryptkeeper\",\"permission\":\"active\"}],\"data\":\"00AEAA4AC15CFD4500000060D234CD3DA06806000000000004454F53000000001A746865206772617373686F70706572206C696573206865617679\"}],\"transaction_extensions\":[]}";

        String json = null;

        try {
            json = abieos.deserializeTransaction(hex);
        } catch (DeserializeTransactionError err) {
            err.printStackTrace();
        }

        assertNotNull(json);
        assertEquals(json, jsonResult);

    }

    @Test
    public void hexToJsonAbiTokenTransfer() {
        String hex = "00AEAA4AC15CFD4500000060D234CD3DA06806000000000004454F53000000001A746865206772617373686F70706572206C696573206865617679";
        String jsonResult = "{\"from\":\"cryptkeeper\",\"to\":\"brandon\",\"quantity\":\"42.0000 EOS\",\"memo\":\"the grasshopper lies heavy\"}";
        String abi = "{\"version\":\"eosio::abi/1.0\",\"types\":[{\"new_type_name\":\"account_name\",\"type\":\"name\"}],\"structs\":[{\"name\":\"transfer\",\"base\":\"\",\"fields\":[{\"name\":\"from\",\"type\":\"account_name\"},{\"name\":\"to\",\"type\":\"account_name\"},{\"name\":\"quantity\",\"type\":\"asset\"},{\"name\":\"memo\",\"type\":\"string\"}]},{\"name\":\"create\",\"base\":\"\",\"fields\":[{\"name\":\"issuer\",\"type\":\"account_name\"},{\"name\":\"maximum_supply\",\"type\":\"asset\"}]},{\"name\":\"issue\",\"base\":\"\",\"fields\":[{\"name\":\"to\",\"type\":\"account_name\"},{\"name\":\"quantity\",\"type\":\"asset\"},{\"name\":\"memo\",\"type\":\"string\"}]},{\"name\":\"account\",\"base\":\"\",\"fields\":[{\"name\":\"balance\",\"type\":\"asset\"}]},{\"name\":\"currency_stats\",\"base\":\"\",\"fields\":[{\"name\":\"supply\",\"type\":\"asset\"},{\"name\":\"max_supply\",\"type\":\"asset\"},{\"name\":\"issuer\",\"type\":\"account_name\"}]}],\"actions\":[{\"name\":\"transfer\",\"type\":\"transfer\",\"ricardian_contract\":\"---\\ntitle: Token Transfer\\nsummary: Transfer tokens from one account to another.\\nicon: https://cdn.testnet.dev.b1ops.net/token-transfer.png#ce51ef9f9eeca3434e85507e0ed49e76fff1265422bded0255f3196ea59c8b0c\\n---\\n\\n## Transfer Terms & Conditions\\n\\nI, {{from}}, certify the following to be true to the best of my knowledge:\\n\\n1. I certify that {{quantity}} is not the proceeds of fraudulent or violent activities.\\n2. I certify that, to the best of my knowledge, {{to}} is not supporting initiation of violence against others.\\n3. I have disclosed any contractual terms & conditions with respect to {{quantity}} to {{to}}.\\n\\nI understand that funds transfers are not reversible after the {{$transaction.delay_sec}} seconds or other delay as configured by {{from}}'s permissions.\\n\\nIf this action fails to be irreversibly confirmed after receiving goods or services from '{{to}}', I agree to either return the goods or services or resend {{quantity}} in a timely manner.\"},{\"name\":\"issue\",\"type\":\"issue\",\"ricardian_contract\":\"\"},{\"name\":\"create\",\"type\":\"create\",\"ricardian_contract\":\"\"}],\"tables\":[{\"name\":\"accounts\",\"index_type\":\"i64\",\"key_names\":[\"currency\"],\"key_types\":[\"uint64\"],\"type\":\"account\"},{\"name\":\"stat\",\"index_type\":\"i64\",\"key_names\":[\"currency\"],\"key_types\":[\"uint64\"],\"type\":\"currency_stats\"}],\"ricardian_clauses\":[],\"error_messages\":[],\"abi_extensions\":[],\"variants\":[]}";

        String json = null;

        try {
            AbiEosSerializationObject serializationObject = new AbiEosSerializationObject("eosio.token", "transfer", null, abi);
            serializationObject.setHex(hex);
            abieos.deserialize(serializationObject);
            json = serializationObject.getJson();
        } catch (DeserializeError err) {
            err.printStackTrace();
        }

        assertNotNull(json);
        assertEquals(json, jsonResult);

    }

    @Test
    public void jsonToHexTransaction2() {
        String json = "{\n" +
                "\"expiration\" : \"2019-02-12T20:35:38.000\",\n" +
                "\"ref_block_num\" : 60851,\n" +
                "\"ref_block_prefix\" : 1743894440,\n" +
                "\"max_net_usage_words\" : 0,\n" +
                "\"max_cpu_usage_ms\" : 0,\n" +
                "\"delay_sec\" : 0,\n" +
                "\"context_free_actions\" : [],\n" +
                "\"actions\" : [\n" +
                "{\n" +
                "\"account\" : \"eosio.assert\",\n" +
                "\"name\" : \"require\",\n" +
                "\"authorization\" : [],\n" +
                "\"data\" : \"CBDD956F52ACD910C3C958136D72F8560D1846BC7CF3157F5FBFB72D3001DE4597F4A1FDBECDA6D59C96A43009FC5E5D7B8F639B1269C77CEC718460DCC19CB30100A6823403EA3055000000572D3CCDCD0143864D5AF0FE294D44D19C612036CBE8C098414C4A12A5A7BB0BFE7DB1556248\"\n" +
                "},\n" +
                "{\n" +
                "\"account\" : \"eosio.token\",\n" +
                "\"name\" : \"transfer\",\n" +
                "\"authorization\" : [\n" +
                "{\n" +
                "\"actor\" : \"cryptkeeper\",\n" +
                "\"permission\" : \"active\"\n" +
                "}\n" +
                "],\n" +
                "\"data\" : \"00AEAA4AC15CFD4500000060D234CD3DA06806000000000004454F53000000001A746865206772617373686F70706572206C696573206865617679\"\n" +
                "}\n" +
                "]\n" +
                ",\n" +
                "\"transaction_extensions\" : []\n" +
                "}";

        String hexResult = "1A2E635CB3EDA8B7F167000000000290AFC2D800EA3055000000405DA7ADBA0072CBDD956F52ACD910C3C958136D72F8560D1846BC7CF3157F5FBFB72D3001DE4597F4A1FDBECDA6D59C96A43009FC5E5D7B8F639B1269C77CEC718460DCC19CB30100A6823403EA3055000000572D3CCDCD0143864D5AF0FE294D44D19C612036CBE8C098414C4A12A5A7BB0BFE7DB155624800A6823403EA3055000000572D3CCDCD0100AEAA4AC15CFD4500000000A8ED32323B00AEAA4AC15CFD4500000060D234CD3DA06806000000000004454F53000000001A746865206772617373686F70706572206C69657320686561767900";

        String hex = null;

        try {
            hex = abieos.serializeTransaction(json);
        } catch (SerializeTransactionError err) {
            err.printStackTrace();
        }

        assertNotNull(hex);
        assertEquals(hex, hexResult);
    }

    @Test
    public void testContextError() {
        try {
            abieos.destroyContext();
            String err = abieos.error();
            fail("Should have thrown an error because the context was null.");
        } catch (SerializationProviderError ace) {

        }
    }
}
