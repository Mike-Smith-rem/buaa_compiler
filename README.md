# 2021-秋  编译器实验设计文档

## 一、文法分析

### 1、设计

​		文法分析的目的实际上就是了解Syst这一门课程组所定义的语言的结构，从而更加有利于之后的分析。因此实际上只需要按照文档中的说明进行设计，而不需要考虑太多。下面给出一个示例程序：

```c
const int const_1 = 0;
const int const_2 = 1, const_3 = 2;
int a;
int b, c = 0;
int func_1(int st, int ed){
    const int mod = 2;
    while (st != ed) {
        st = st + 1;
    }
    printf("func_1 complete: int func, int, int\n");
    return 0;
}
void func_2(int a_number, int b_number) {
    printf("func_2 complete: void func, int, int\n");
}
void func_3(){
    return;
}
int func_4() {
    int b_1;
    b_1 = func_1(1, 10);
    const int a_1 = 0;
    func_2(1, a);
    func_3();
    printf("func_3 complete: void func\n");
    return a_1 + b_1;
}
int func_5() {
    printf("func_5 complete: int func\n");
    return 0;
}

int main() {
    printf("学号\n");
    func_4();
    printf("func_4 complete: int func\n");
    func_5();
    printf("global const int: const_1 = %d\n", const_1);
    printf("global const int int: const_2 = %d, const_3 = %d\n", const_2, const_3);
    printf("global int: a = %d\n", a);
    printf("global int, int: b = %d, c = %d\n", b, c);
    return 0;
}
```

## 二、词法分析

### 1、设计

​		词法分析过程需要的写的代码也不多，这一阶段的主要任务还是在于对于Syst语言的理解。因此，此过程只需要考虑以下因素：

- 构建词法表
- 文件的读取和输出
- 对于字符串的逐一分析
- 结果保存（java中使用hashMap）

​		相对而言第一和第二次作业较为简单，也是为之后的语法分析做准备。

​		因此设计如下：

- 设计两个类，一个类为`CompilerLoad`，用于构建词法表`WordMap`以及构造文件的读出和输入函数；另外一个类为`LexAnalyse`，专门用于词法分析（字符串逐一分析），并将结果保存在`LexMap`中

- `CompilerLoad`位于`src/CompilerLoad`文件夹下，对于三个函数定义如下：

  ```java
  //类的静态变量
  /*词法表wordMap*/
  public static HashMap<String, String> wordMap;
  /*源程序*/
  public static List<String> OriginFile;
  
  //该方法用于读取文件,返回每一行组成的list
  static List<String> 
  	getFileContent(String File) 
      throws IOException {
  	BuildMap();
  	BufferedReader br = new BufferedReader(new 	InputStreamReader(new FileInputStream(File)));
  	List<String> FileContent = new ArrayList<>();
  	String line;
  	while ((line = br.readLine()) != null) {
  		FileContent.add(line);
  	}
  	return FileContent;
  }
  
  //该方法用于输出文件
  static void 
  OutputFileContent(String File) throws IOException {
      OutputStreamWriter owr = new OutputStreamWriter(new FileOutputStream(File));
      for (String report : LexAnalyse.LexMap) {
          owr.write(report);
          owr.flush();
      }
      owr.close();
  }
  
  //该方法用于构建表格
  static void buidMap() {
  	....
  }
  
  //类的启动方法
  static void run(){
  	String FileName = "testfile.txt";
      OriginFile = getFileContent(FileName);
      LexAnalyse.run();
      String OutFile = "output.txt";
      OutputFileContent(OutFile);
  }
  ```

- `LexAnalyse`位于`src/LexAnalyse`文件下

  ```java
  //变量定义
  //引入词法表
  static HashMap<String, String> wordMap = CompilerLoad.wordMap;
  //词法输出表
  static Queue<HashMap<MyString, String>> LexMap = new LinkedList<>();
  
  //启动方法
  public static void run() {
  	List<String> OriginFile 
  		=CompilerLoad.OriginFile;
  	int state = 1;
  	//state = 0 说明在注释环节
  	//state = 1 正常读取
  	//state = -1 证明在读取字符串
  	for (String sentence : OriginFile) {
  		CompilerLoad.current_line += 1;
  		state = analyse(sentence, state);
  	}
  	CompilerLoad.current_line = 1;
  }
  
  //分析函数
  static int analyse(String sentence, int state) {
      //逐一分析
      StringBuilder Queue = new StringBuilder();
      char[] words = sentence.toCharArray();
      for (int i = 0; i < words.length; i ++) {
          switch(words[i]) {
                  ..
          }
      }
  }
  
  //结果保存
  static void checkToken(String word) {
      HashMap<MyString, String> token = new HashMap<>();
      MyString tk;
      ...
      token.put(tk, word);
      LexMap.offer(token);
  }
  ```
  

​		将这两个函数完成之后，词法分析便能够顺利通过了。

### 2、设计改善

​		本次词法分析设计改善主要用于之后在错误处理中进行定位。因为符号表的建立是在语法分析阶段之后的，而语法分析的依据是来自于`LexMap`而不再是`OriginFile`，因此，为了能够在错误处理中定位到**错误所在行数**，自建了一个类$MyString$。用于词法分析中**保存该`word`所在行数**。



​		MyString的定义位于`src/LexAnalyse`文件下，和`LexAnalyse.java`位于同一目录。

```java
public class MyString {
    //和string同定义
    String token;
    int line;

    public MyString(String token, int line) {
        this.token = token;
        this.line = line;
    }

    public String getToken() {
        return token;
    }

    public int getLine() {
        return line;
    }

    public boolean equals(String target) {
        return token.equals(target);
    }

    public void refreshLine() {
        CompilerLoad.current_line = line;
    }
}
```

​		因此在结果保存中使用的是`HashMap<MyString, String>`的结构，当进行错误处理时，只需要通过获取当前Token，然后得到Token中MyString内的Line属性即可定位行数。

## 三、语法分析

### 1、设计

#### （1）结构

​		当已经有了词法分析的基础时，此时我们已经获得了`LexMap`，保存并翻译了所有的Token，因此可以新建一个文件夹`GrammarAnalyse`，其中用于存放对`LexMap`进行 分析的分析函数，而保证了程序结构的完整性。

​		另外可以看到需要将每一个非终结符都进行分析，由于java的特性，一个类文件中最好只有一个类，因此需要建立从`AddExp`直到`VarDef`等三十多个文件进行逐一分析。另外由于文件用途的相似性，可以建立一个公共类，从而通过继承减少代码的数量。

​		因此文件结构如下：

- `Grammar/GrammarAnalyse`文件用于定义每一个非终结符所定义的类，从`AddExp`直到`VarDef`，用于递归下降分析。
- `Grammar`目录下存在一个接口文件（类型仍为class）`GrammarInterface`，里面包含了在通常分析中相同的代码模块。



​		另外需要说明的是，`LexMap`采取的队列结构，也就是java中的`Queue`。由于输出需要从头到尾输出，而另一方面需要从左到右进行读入，因此采取队列的结构现阶段是合理的（但是另一方面考虑可能在最后代码生成过程中这种结构仍然不太适用）。

​		本次作业可以不建立符号表，因此只需要如上结构即可。

#### （2）分析方式

​		语法分析相对复杂，而且可能出现回溯的问题，因此本次设计中主要采用两种思路：递归下降和预测。

##### I、父类定义

​		首先对父类进行定义：

```java
public class GrammarInterface {
    //获得LexMap
    public Queue<HashMap<MyString, String>> LexMap;
    //section用于构造树结构，也就是将递推得到的其他符号添加进当前的非终结符
    public ArrayList<Object> section = new ArrayList<>();
	//output.txt中需要输出的文件
    public Queue<HashMap<MyString, MyString>> OutputMap = new LinkedList<>();
    
    
    //需要子类覆写的方法
    public void analyse() {

    }
	
    //子类共有的构造方法
    public GrammarInterface() {
        LexMap = LexAnalyse.getLexMap();
    }
    
    //以下是通用可以继承的方法
    
    //用于比较
    public boolean equals(HashMap<MyString, String> word, String target) {
        ...
    }

    //获得map中前一部分
    public String getToken(HashMap<MyString, String> word) {
        ...
    }
    
    
	//获得map中后一部分
    public String getContent(HashMap<MyString, String> word) {
        ...
    }
}
```

​		通过对于父类的定义，子类在描述中可以省去很多麻烦，基本只需要覆写`analyse`方法即可。

##### II、递归下降

​		根据对于文法的定义，从`CompUnit`依次进行递归下降分析。下面以`LVal`的递归下降分析为例：

```java
public class LVal extends GrammarInterface {
    //LVal -> Ident {'[' Exp ']'}

    @Override
    public void analyse() {
        //Ident
        Ident identF = new Ident();
        identF.analyse();
        section.add(identF);

        //[exp]
        while (equals(LexMap.element(), "LBRACK")) {
            //[
            section.add(LexMap.poll());
            //Exp
            Exp exp = new Exp();
            exp.analyse();
            section.add(exp);
            //]
            section.add(LexMap.poll());
        }
        
        //最后加入output.txt
        //可以任意用一个标志符来区分原来的LexMap单元和添加的`<>`单元
        ....
    }
}
```

​		需要完成的过程基本就是**新建非终结符分析单元、进行分析、添加新建单元到当前section中，最后加入output.txt对应的list中。**通过这样一个方式，从而可以直接进行递归下降分析，而且通过section也搭建好了子元素。

##### III、多用预测，避免回溯

​		显然递归下降中是需要提前了解需要下降到哪一个非终结符。而有些文法可能其`FIRST`集合存在相同单元，因此无法通过LL（1）分析而直接确定从哪一个区域进行递归下降，而同时为了避免回溯（回溯基本没有考虑，感觉实现过程很麻烦），因此可以多偷看一些单元，从而能够确定应该从哪个非终结符进行递归下降。

​		这里就以`CompUnit`为例：

```java
public class CompUnit extends GrammarInterface {

    @Override
    public void analyse() {
        declAnalyse();
        funcAnalyse();
        mainAnalyse();
    }

    public void declAnalyse() {
        while (true) {
            //通过循环查看三个量
            //这里实际上也体现出Queue结构的劣势
            HashMap<MyString, String> firstToken = null;
            HashMap<MyString, String> secondToken = null;
            HashMap<MyString, String> thirdToken = null;
            int time = 0;
            for (HashMap<MyString, String> key : LexMap) {
                firstToken = time == 0 ? key : firstToken;
                secondToken = time == 1 ? key : secondToken;
                thirdToken = time == 2 ? key : thirdToken;
                time += 1;
                if (time == 3) {
                    break;
                }
            }

            assert firstToken != null && secondToken != null && thirdToken != null;
            
            //之后通过if来分析需要往哪个方向进行递推下降
            if (equals(firstToken, "CONSTTK")) {
                Decl decl = new Decl();
                section.add(decl);
                decl.analyse();
            }
            else if (equals(firstToken, "INTTK")
                    && equals(secondToken, "IDENFR")
                    && !equals(thirdToken, "LPARENT")) {
                Decl decl = new Decl();
                section.add(decl);
                decl.analyse();
            }
            else {
                return;
            }
        }
    }
    
    public void funcAnalyse() {...}
    public void mainAnalyse() {...}
}
```

​		可以看到`CompUnit`无论是`Decl`还是`funcDef`都会使得前两个Token一个为Type一个为Ident，因此通过提前查看三个Token，从而能够分析出需要从哪个方向进行递归下降。



​		总而言之，实现以上几点，语法分析能够较为轻松的完成。

### 2、设计改善

​		这里的设计改善主要针对于某些非终结符。在每一个非终结符中虽然已经利用了`section`这一个保存`Object`的数组存入其递推单元，但是之后在值类型、错误分析中仅仅通过`section`是不够的。比如对于`LVal`，由于其递推式：

```
LVal -> Ident {'[' Exp ']'}
```

​		实际上可以直接新建一些本地变量保存其值类型等，从而能够为错误处理提供方便。具体的说明还是放在错误处理中。（因为在写错误处理的时候重构了一次，才有了现在的结构）

## 四、错误处理（debug）

### 1、重构（以上版本是重构后的内容）

​		之前的语法分析版本我并没有分出文件结构，所有的文件都在`src`目录下（当时学习OO的时候没有专注解决的问题），因而加上所有的非终结符类然后加上所有的错误处理A~M有50多个文件看过来确实过于困难，而且在debug过程中基本无解。另外之前并没有构建树（也就是没有加上section单元），无法从一个非终结符了解到其子结构，这样也很难进行错误处理。因此在错误处理延期后，放弃了已有的程序，而重新规划结构，故而进行了重构。

​		重构后的文件结构如下：

![1636440051467](C:\Users\abb255\AppData\Roaming\Typora\typora-user-images\1636440051467.png)

​		另外新加了很多单元，从而使得代码结构和层次更加清晰。

### 2、设计思路

​		预测手段在错误处理中出现了难题。因此错误的存在使得预测可能预测到意想不到的单元。考虑到错误的出现形式多样，因此实际上课程组给出了一定限制，这也使得我的预测手段仍然具有一线生机。

​		首先在未改动原本程序的情况下需要新建错误处理单元，同语法分析过程，设定了父类`WrongFormatAnalyse`和子类文件夹`WrongAnalyse`。子类文件夹包括从A到M的所有错误处理单元。

​		另外本次需要构建符号表。按照课堂上的说明，选择栈式结构是可行的，而本人设计时也将函数和变量放在了一起，并设置了一个索引单元。

​		因此本次设计分为以下部分：

- 在原本程序中构建符号表
- 在原本程序中增加行数定位的方法
- 在原本程序中增加错误处理单元，以及其相应的调用方式
- 符号表和错误处理的操作过程

### 3、设计

#### （1）符号表构建

​		在Table文件下存在两个文件`Table`和`TableIndex`。其中`Table`表示表的每一个元组，而`TableIndex`表示表的容器。因此在`Table`中只需要定义属性，而`TableIndex`需要完成和外界的交互。

- Table

```java
public class Table {
    //const, var, func
    public String specie = "";
	
    //if const and var
    //ident
    public String name = "";
    //dimen
    public int lev = 0;
	
    
    //if func (name inherent)
    //void int
    public String funcType = "";
    //
    public ArrayList<Table> FParams = new ArrayList<>();
    //if return (use in errorAnalyse)
    public boolean returned = false;
}
```

- TableIndex

```java
public class TableIndex {
    //table变量表
    public static Stack<Table> tables = new Stack<>();
    
    //索引表
    public static Stack<HashMap<Integer, Integer>> index = new Stack<>();
    
    //当前所在的block级别
    public static int cur_index = 1;

    //当前的循环深度
    public static int while_depth = 0;

    static {
        HashMap<Integer, Integer> head = new HashMap<>();
        head.put(cur_index, 0);
        index.push(head);
    }
	
    
    //新建函数时，block级别增加，并添加索引
    public static void loadIndex() {
        TableIndex.cur_index += 1;
        HashMap<Integer, Integer> index = new HashMap<>();
        index.put(TableIndex.cur_index, TableIndex.tables.size());
        TableIndex.index.push(index);
    }

    //函数结束时，block级别降低，并移除索引以及所有临时变量
    public static void pushIndex() {
        int TableSize = TableIndex.index.peek().get(TableIndex.cur_index);
        while (TableIndex.tables.size() != TableSize) {
            TableIndex.tables.pop();
        }
        TableIndex.index.pop();
        TableIndex.cur_index -= 1;
    }
	
    //用于外界查找
    public static Table searchTable(String name) {
        for (int i = tables.size() - 1; i >= 0; i--) {
            if (tables.get(i).name.equals(name)) {
                return tables.get(i);
            }
        }
        return null;
    }
}
```

​		这样下来一个相对完整的符号表体系就构建完成了，并且符号表能够监控分析过程中的循环深度以及block级别，实现了栈式结构，为错误处理的**作用域分析**提供了有力保障。



#### （2）行定位

​		考虑处理这一问题时，我也考虑直接用全局变量来进行监测。但是问题在于这个全局变量如何能够获得当前行数，或者通过哪种方式得到当前行数。因此在上文中我定义了`MyString`类就是为行定位服务的。因为自身的程序只有在词法分析扫描时会直接扫描源程序，而后续扫描都将基于前一次扫描得到的结果，此时可能会丢失行信息。

​		因此在每次错误处理分析之前通过`MyString`更新所在行，将其赋给全局变量`current_line`，从而能够实现行定位。

​		在`CompilerLoad`中加入了新的方法：

```java
public static int getCurrent_line() {
    //获取顶端元素
    HashMap<MyString, String> item = LexAnalyse.getLexMap().element();
    for (MyString string : item.keySet()) {
        string.refreshLine();
    }
    return current_line;
}
```

​		其中`refreshLine`在`MyString`中实现：

```java
public void refreshLine() {
    CompilerLoad.current_line = line;
}
```

​		通过词法分析将行信息提前保存在MyString中，继而为错误处理的行定位提供了方便。



#### （3）错误处理单元和调用

##### I、声明

​		首先说一下父类`WrongFormatAnalyse`，父类仅仅帮助子类定义了变量：

```java
public class WrongFormatAnalyse {
    public static ArrayList<String> errorReport;
    public static Stack<Table> tables;
    public static int cur_index;
    public static Stack<HashMap<Integer, Integer>> index;
    public static int while_depth;

    public WrongFormatAnalyse() {
        //在CompilerLoad中增加errorReport的list单元，用于output输出和错误处理输出
        errorReport = CompilerLoad.errorReport;
        tables = TableIndex.tables;
        cur_index = TableIndex.cur_index;
        index = TableIndex.index;
        while_depth = TableIndex.while_depth;
    }
}
```

​		实际上必要性不大。

​		而对于子类的实现实际上需要和原本语法分析的程序建立好连接，确定好错误处理单元所传入的参数，比如d类错误传入三个参数（函数名称、函数需要变量的数量和实际传入的数量），而e类错误传入两个参数（函数名称、传入的参数构成的表），不具有统一性，从而能够实现错误处理。这里的细节就不做过多说明了。

##### II、调用

​		调用的方式是在语法分析程序中进行调用的，而修改的地方是根据文档中说明的地方进行修正的。

![1636443098485](C:\Users\abb255\AppData\Roaming\Typora\typora-user-images\1636443098485.png)

​		一般来说可以直接找到对应的语法分析单元进行修正，比如对于a类错误直接在`formatString`中修改：

```java
public class FormatString extends GrammarInterface {
    public int numberOfD = 0;
    public String content = "";

    @Override
    public void analyse() {
        content = getContent(LexMap.element());
		
		//以下两行直接调用错误处理
        A_FormatString formatString = new A_FormatString();
        formatString.check(content, CompilerLoad.getCurrent_line())；
        
        
        for (int i = 0; i < content.length(); i ++) {
            if (content.charAt(i) == '%'
                    && i + 1 < content.length()
                    && content.charAt(i + 1) == 'd') {
                numberOfD += 1;
                i += 1;
            }
        }
        section.add(LexMap.poll());
    }
}
```

​			但是有的需要寻找合适的位置以及添加相应的变量才能实现。

​			比如在处理`break`和`continue`的时候需要新建变量`while_depth`（在符号表索引中建立），从而才能直接在`break`和`continue`中调用，实现方式如下：

```java
public class M_UnNeededToken extends WrongFormatAnalyse {
    public void check(int current_line) {
        //如果当前没有while循环
        if (while_depth == 0) {
            errorReport.add(current_line + " m\n");
        }
    }
}
```

​		而对于g类错误则必须在整个函数结束之后才能调用。

​		因而g类错误的调用我放在了`CompUnit`下面：

```java
//以main方法为例
public void mainAnalyse() {
    MainFuncDef mainFuncDef = new MainFuncDef();
    section.add(mainFuncDef);
    mainFuncDef.analyse();
		
    //函数分析完成后调用
    G_LackReturn g_lackReturn = new G_LackReturn();
    g_lackReturn.check(CompilerLoad.current_line);
}
```

​		而g类错误本身的定义也需要进行对应更改：

```java
{
	public void check(int current_line) {
   		//通过查找刚刚结束的函数,而不是在}之前
        Table func = getLatestFunc();
        boolean wrong = false;
        if (func != null && func.specie.equals("func")
                && !func.funcType.equals("void")
                && !func.returned) {
            wrong = true;
        }
        if (wrong) {
            errorReport.add(current_line + " g\n");
        }
    }

    public Table getLatestFunc() {
        for (int i = tables.size() - 1; i >= 0; i--) {
            if (tables.get(i).specie.equals("func")) {
                return tables.get(i);
            }
        }
        return null;
    }
}
```

​		这样的细节需要根据自身的程序进行决定，不具有统一性，因而只提出几个代表进行说明。

#### （4）操作和实现的一些细节（冲突解决）

​		这里所谓的细节，就是我在**进行预测和错误处理可能产生的冲突**。因为**存在错误使得预测可能得到意想不到的结果**。

​		这里的我主要以`return `为例：

​		可能出现的`return`错误形式：

```java
return 
return [exp]
return ;
```

​		如果按照我的预测方式我会通过是否下一个元素为`;`从而判断进行`Exp`分析。如果是那么直接将`;`加入section， 否则进行`Exp`分析。但是加入错误处理后，可能不存在分号而产生歧义，此时再次进行`Exp`分析势必会产生`RunTimeError`。从而出错。

​		另一方面`Exp`本身是很难分析其起始变量的，从而使得解决这样的冲突变得十分困难。

​		这里最后考虑到**不会出现恶意换行**，因此我才解决了难题：

```java
//RETURN
case "RETURNTK":
	//更新行数
	CompilerLoad.getCurrent_line();
	//用于g类错误分析
	Table func = getLatestFunc();
	if (func != null && func.funcType.equals("void")) {
    	func.returned = true;
	}
	section.add(LexMap.poll());

	//分析过程
	//getline获取return后面Token行数
	//如果行数相同，证明后面是exp，可以进行exp分析
	if (!equals(LexMap.element(), "SEMICN")
    	&& getLine(LexMap.element()) == 		
        CompilerLoad.current_line) {
        
        //这里借用funcRParam进行分析，（funcRParam中定义了值类型，用于f类错误）
        //之后会进一步改进，将值类型分析其转移到exp
    	FuncRParams funcRParams = new FuncRParams();
    	funcRParams.analyse();
    	...
	}
	//;
```

​		这样的问题不止一处，但是也不是很多。通过条件的限制，从而能够完成对错误处理的分析和判断。