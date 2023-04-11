import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        LinkedList<String> code=new LinkedList<>();

        //WAW
//        code.add("ADD RAX RBX RCX");
//        code.add("SUB RBI RAI RCX");
        //WAR
//        code.add("ADD RAX RBX RCX");
//        code.add("MUL RAI RDX RAX");
        //RAW
//          code.add("ADD RBX RCX RAX");
//          code.add("MUL RAX RDX RAI");

        //NO_HAZARD
        code.add("ADD RAX RBX RDX");
        code.add("MUL RAX RBX RCX");

        Pipeline pipeline=new Pipeline(code,true);
        pipeline.execute();
    }
}