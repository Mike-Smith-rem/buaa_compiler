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
    printf("19373686\n");
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



​		`MyString`的定义位于`src/LexAnalyse`文件下，和`LexAnalyse.java`位于同一目录。

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

- `TableIndex`

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

​		通过词法分析将行信息提前保存在`MyString`中，继而为错误处理的行定位提供了方便。



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



## 五、代码生成

​		代码生成考核中我选择的是PCode，而在提交的代码中该部分所对应的文件夹为`src/CodeLoad`。该文件夹下包括`MidCodeGenerate`（中间代码生成）、`PCodeGenerator`（最终代码生成）、`PCodeAnalyser`（解释器）以及解释器执行中需要的符号表`PCodeTable`。通过这样一层设计，从而代码结构十分清晰；同时，设计了最终代码和中间代码也使得在Debug的过程中容易许多。

​		因而根据文件结构，对于代码生成分成四个部分进行分析。

![1639622537843](C:\Users\abb255\AppData\Roaming\Typora\typora-user-images\1639622537843.png)

### 		1、中间代码

#### 		（一）文件结构

​		中间代码对应的文件夹为`MidCodeGenerate`，该文件夹的目录如下：

![1639622639205](C:\Users\abb255\AppData\Roaming\Typora\typora-user-images\1639622639205.png)

​		由于通过语法分析和错误处理后，**得到的是一个类似语法树的结构，此时的根节点是整个编译起始非终结符`CompUnit`，在语法分析设置的section结构中存储了子节点**，因此仍需要进行一遍类似于语法分析过程的每一个非终结符的分析，以便分析一遍生成中间代码，从而需要如上的结构。

​		从上文可知，个人在定义section的时候存储的数据类型是**`Object`**，因此此时java中的`instanceOf`关键词会非常重要。

​		另外生成中间代码的过程中需要进行部分的计算和生成中间变量等，因此也必须需要一个临时的符号表来进行插入和查询。

#### 	（二）实现原理和过程

​		该过程的目标很明确，就是要生成中间代码，而中间代码的格式参照课程组给出的推荐格式。

- ##### 变量定义和赋值

  比如在通过$varDef$的过程中过程中需要进行如下操作：

  ```java
  //Ident {'[' ConstExp ']'}
  //Ident {'[' ConstExp ']'} = InitVal
  
  public void analyse() {
  	/**
  	* 首先进行树的逻辑分析
      */
  	
  	//获取第一个子节点,识别ident
  	Object item = section.get(0);
  	if (item instanceof Ident) {
  		ident = ((Ident) item).getIdent();
  	}
  	
  	//对于之后的子节点进行分析
  	for (Object it : section) {
  		//如果是ConstExp,则变量维度加一
  		if (it instanceof ConstExp) {
  			lev += 1;
  			//然后进行ConstExp分析
  			Load_ConstExp constExp = new Load_ConstExp();
              ...
  			constExp.analyse();
  			
  			//将分析后的值进行保存
  			length[lev] = constExp.value;
  		}
  		//同上分析
  		else if (it instanceof InitVal) {
  			Load_InitVal initVal = new Load_InitVal();
              ...
  			initVal.analyse();
  			
  			//保存所有的initVal所表示的值。
  			Exps.addAll(initVal.Exps);
  		}
  	}
  	
  	/**
  	* 其次生成中间代码
  	*/
  	addSentence();
  }
  
  public void addSentence() {
      //分成0维、一维、二维三种情况分别考虑
      
  	if (lev == 0) {
          //设置varTable（变量）存入符号表
  		varTable.name = ident;
  		varTable.lev = lev;
          
          //生成中间代码
  		midCode.add("int " + ident);
  		
  	}
  	else if (lev == 1) {
  		...
  		midCode.add("array int " + 
  			ident + "[" + length[lev] + "]");
  	} 
  	else if (lev == 2) {
  		...
  		midCode.add("array int " + ident + "[" + 
  			length[lev - 1] + "]" +"[" + length[lev] + "]");
  	}
  }
  
  //根据实际情况进行赋值操作
  
  private void loadArray() {
      for (MidInterface var : Exps) {
          //临时表中存储初始值
          varTable.level.add(var.value);
          //反映到中间代码上——“#Assign”
          midCode.add(ident + 
                      "[" + i + "]" + " #ASSIGN " + var.name);
      }
  }
  ```

  ​		同样对于常量定义也类似，分成三个部分：首先进行子节点逻辑分析；逻辑分析完成后将分析结果按照推荐的中间代码形式表现出来；同时将变量定义放到符号表中。通过这三层逻辑，较为严密的实现了变量和常量的定义过程。

  

- ##### 函数生成与表示

  函数生成相对而言和变量定义等等类似，这里只需要简单描述即可。

  ```java
  public class Load_FuncDef extends CodeLoad {
      //FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
      public String funcType;
      public String funcName;
      public ArrayList<VarTable> params = new ArrayList<>();
      public FuncTable funcTable = new FuncTable();
  
      @Override
      public void analyse() {
          for (Object item : section) {
              ...
              //和变量定义类似，只需要得到ident可以直接输出函数定义
              if (item instanceof Ident) {
                  ...
                  midCode.add(funcType + " " + funcName + "()");
              }
              
              //另外函数的特点在于需要进行参数分析
              else if (item instanceof FuncFParams) {
                  Load_FuncFParams funcFParams = 
                  	new Load_FuncFParams();
                  
                  funcFParams.analyse();
                  
                  
                  params.addAll(funcFParams.params);
                  funcTable.FParams.addAll(params);
              }
              ...
          }
      }
  }
  
  ```

  在`Load_FuncFParams`中有这样的定义：

  ```java
  @Override
  public void addSentence() {
      //参数和变量类似，需要按照维度进行分析
  	for (VarTable t : params) {
      	if (t.lev == 0) {
          	midCode.add("para int " + t.name);
          }
          else if (t.lev == 1) {
              midCode.add("para array int " + t.name + "[]");
          }
          else if (t.lev == 2) {
              midCode.add("para array int " + t.name + 
              	"[]" + "[" + t.lev2_length + "]");
          }
      }
  }
  ```

  显然上述的定义实现了参数传递。通过以上两个单元，从而能够满足函数定义以及参数传递。

- ##### `Stmt`的分析

  `Stmt`的分析较为复杂，主要涉及到`if`和`while`，以及计算过程。简单分析如下：

  - ###### `if`和`while`，生成标识`Label`

    ```java
    if (item instanceof HashMap) {
        int tempWhile = whileNum;
        int tempIf = ifNum;
        switch (head) {
            //if语句需要完成的是创建label，包括开始与结束
            //由于整段程序中if出现的次数为有限个，因此设计全局静态变量ifNum加以区分
        	case "if" :
                ifNum += 1;
                //起始端
                midCode.add("$if-start-" + tempIf);
                ...
                //条件跳转
                midCode.add("#GOTO $if-end-" + tempIf);
                midCode.add("$if-end-" + tempIf);
                //这里需要考虑到else的存在
                {
                	midCode.add("$else-start-" + tempIf);
                	...
                	midCode.add("$else-end-" + tempIf);
                }
                //末端
               	midCode.add("$if-final-" + tempIf);
               	break;
            
            //while和if同理，另外while用另外一个全局静态变量加以label标识。
           	case "while" :
           		whileNum += 1;
                midCode.add("$while-start-" + tempWhile);
                ...
                midCode.add("#GOTO $while-final-" + tempWhile);
                ...
                midCode.add("$while-end-" + tempWhile);
               	break;
               	
            //break和continue 相对特殊，因为实际上它们所指向的循环语句并非whileNum，因此新建另外一个属性current_while_num加以区分
            case "break" :
            	midCode.add("#GOTO $while-final-" + current_While_Num);
                break;
            
            
            case "continue" :
            	midCode.add("#GOTO $while-start-" + current_While_Num);
                break;
    	}
    ```

    ​		这里使用label生成的时候使用了一个小技巧：由于整个程序中不会出现`$`这一标识符，因而可以作为label标志符的起始字母；同理，进行指令操作的时候（如`Assign`），由于整个程序中不会出现`#`，从而可以使用#来表示指令操作，这种方式在之后生成中间变量以及在写解释器进行区分的时候相当重要。

    ​		其次，label关乎三类：label-start、label-end、label-final。**其中final才是最终if、while语句结束的标志，而end只会出现在if中，用于条件跳转过程中存在else的情况下不会直接结束if模块。**

    ​		关于break和continue如上代码所述。

    ​		通过这种方式，从而构建了条件跳转语句和循环语句的整体框架，便于解释器进行分析。

    

  - ###### 计算过程，`Replace`生成中间变量

    ​	中间变量的生成是一个非常关键的问题。下面是我对于中间变量的理解：

    - 表示方式：`@ + 类名 + 出现次数`

      比如在`AddExp`中产生的中间变量，并且是第二次产生，那么该中间变量表示为`@AddExp1`。
      
    - 出现位置
    
      中间变量主要在`Exp`以及其子类中产生。其中也有一些特殊的地方去定义中间变量
    
    - 执行过程
    
      利用`#Replace`进行执行。
    
    以上是对于中间变量生成的简单描述，部分代码如下：
    
    ```java
    /**
    * AddExp中定义
    */
    
    public void addSentence() {
        //如果此时只有一个子节点
        if (sonQueue.size() == 1) {
            midInterface = sonQueue.element();
        } 
        //否则设置中间变量，并表示
        else {
            midInterface = new MidTable();
            midInterface.name = "@AddExp" + varNum;
            varNum += 1;
            String op;
            MidInterface a;
            MidInterface b;
            int size = 0;
            if (op.equals("+")) {
            	midCode.add(midInterface.name + " #REPLACE " 
            		+ a.name + " + " + b.name);
            } 
            else if (op.equals("-")) {        
                midCode.add(midInterface.name + " #REPLACE " 
                	+ a.name + " - " + b.name);
            } 
        }
    }
    ```
    
    

- ##### 其他细节

  ​	这里就不一一详细说明每一个地方是怎么定义的、怎么书写的，因此在这里简单描述一下。
  
  - 读取和写入
  
    只需要检测`getint`进行转化生成中间代码；对于写入注意区分`str`直接写入还是值写入。
  
  - 函数调用和传参
  
    同上，可以直接在对应的位置进行，转化为对应的中间代码即可。

#### 	（三）临时符号表的构建

​		这里同语法分析类似，同样构建了临时的符号表；这里构建的临时符号表有两个用处：

1. 计算常量。

   很多编译器都是提前将常量计算好之后再转入运行栈，因此同样的本次设计也添加了提前计算出常量的代码，而常量的存储需要符号表。

2. 存储中间变量

   因为中间代码生成需要中间变量加持，所以个人觉得还是需要一个符号表来临时放置一下中间变量。但是最后似乎作用不是很明显。



​		虽然说中间代码生成也使用了临时符号表，但是在完成整个作业之后感觉其实际意义可能主要在于计算常量中。

#### 	（四）生成结果

​		这里简单给出程序示例，从而展现出中间代码的生成。

​		源程序是输出指定数字的阶乘。

- 源程序

  ```c
  int array_1[10], array_2[10];
  const int a = 3, b[3] = {1, 2 + 4, 1};
  
  int fib(int i) {
      if (i == 1) {
          return 1;
      }
      else {
          return i * fib(i - 1);
      }
  }
  int main() {
      int a;
      a = getint();
      printf("%d! is %d\n", a, fib(a));
      return 0;
  }
  ```

- 中间代码

  ```java
  //注释
  
  //全局变量1-2行
  array int array_1[10]
  array int array_2[10]
  const int a
  a #ASSIGN 3
  @AddExp0 #REPLACE 2 + 4
  const array int b[3]
  b[0] #ASSIGN 1
  b[1] #ASSIGN 6		/*这个地方正常应该是`b[1] #ASSIGN @AddExp0*/
      				/*但是由于是constInitVal从而直接计算了*/
  b[2] #ASSIGN 1
  
  //函数定义4
  int fib()
  para int i
  
  //if语句5-7
  $block-start
  $if-start-0
  @EqExp0 #REPLACE i == 1
  @EqExp0 #GOTO $if-end-0
  $block-start
  #RETURN 1
  $block-final
  #GOTO $if-final-0
  $if-end-0
      
  //else语句8-10
  $else-start-0
  $block-start
  @AddExp1 #REPLACE i - 1
  #CALL fib
  #PUSH @AddExp1
  @Func0 #REPLACE fib(@AddExp1)
  @MulExp0 #REPLACE i * @Func0
  #RETURN @MulExp0
  $block-final
  #GOTO $if-final-0
  $else-end-0
  $if-final-0
  $block-final
      
  //主程序
  int main()
  $block-start
  int a
  #READ a
  #CALL fib
  #PUSH a
  @Func1 #REPLACE fib(a)
  #WRITESTR 
  #WRITEVAR a
  #WRITESTR ! is 
  #WRITEVAR @Func1
  #WRITESTR \n
  #RETURN 0
  $block-final
  
  ```

  ​		中间代码生成后整个源文件被分析得已经足够清楚了，那么剩下的只需要将中间代码的格式稍微转换成自己想要设计的目标代码，然后 书写解释器，整个工作就完成了。
  
  ​		中间代码生成个人认为是除了解释器中最重要的一步；可以看到，生成中间代码后整个源文件的逻辑性更加容易分析。实际上中间代码生成的代码量很足，但是也确实为目标代码和解释器提供了方便（尤其是目标代码）。在此基础下，对于目标代码的分析中，可以看到实现目标代码生成相当容易。

### 		2、目标代码

#### 	（一）文件结构

​		在`src/CodeLoad/PCodeGenerator/PCodeGenerate.java`中实现了从中间代码转变为目标代码。

#### 	（二）实现过程

- ##### PCode定义

  经过几番修改，最终对于本次设计的PCode定义如下，总共为15条指令。

  - ###### 变量（常量）定义

    结构：`#VarDef` + 变量名称 + 变量类型 + 【一维长度】 + 【二维长度】

    举例：

    ```java
    Origin:
    	int a[3][3], b;
    
    midCode:
    	array int a[3][3]
    	int b
    
    Target:
    	#VarDef a int 3 3 
    	#VarDef b int
    ```

  - ###### 赋值操作

    结构：`#Assign` + 被赋值变量（左值）+ 赋值量（右值）

    举例：

    ```java
    Origin:
    	const int a[2] = {1, 2};
        int b;
        b = 1
        
    midCode:
        const array int a[2]
        a[0] #ASSIGN 1
        a[1] #ASSIGN 2
        int b
        b #ASSIGN 1
        
    Target:
    	#VarDef a int 2
        #Assign a[0] 1
        #Assign a[1] 2
        #VarDef b int
        #Assign b 1
    ```

    

  - ###### 中间变量替换

    结构：`#Repalce` + 新生成中间变量 + 函数/数组

    ​		   `#Replace` + 新生成中间变量 + op（运算符）+ 变量

    ​		   `#Replace` + 新生成中间变量 + 变量1 + op + 变量2

    举例：

    ```java
    Origin:
    	int a[2][2] = {{1, 2}, {1, 2}};
        int b;
        b =  - a[1][1];
        
    midCode:
    	array int a[2][2]
        a[0] #ASSIGN 1
        a[1] #ASSIGN 2
        a[2] #ASSIGN 1
        a[3] #ASSIGN 2
        int b
        @TwoArray-0 #REPLACE 1 * 2
        @TwoArray-0 #REPLACE 1 + @TwoArray-0
        @TwoArray-0 #REPLACE a[@TwoArray-0]
        @UnaryExp0 #REPLACE - @TwoArray-0
        b #ASSIGN @UnaryExp0
    
    Target:
    	#VarDef a int 2 2
        #Assign a[0] 1
        #Assign a[1] 2
        #Assign a[2] 1
        #Assign a[3] 2
        #VarDef b int
        #Replace @TwoArray-0 1 * 2
        #Replace @TwoArray-0 1 + @TwoArray-0
        #Replace @TwoArray-0 a[@TwoArray-0]
        #Replace @UnaryExp0 - @TwoArray-0
        #Assign b @UnaryExp0
    ```

    

  - ###### 函数定义

    结构：`#FuncDef` + 函数名称 + 函数类型

    举例：

    ```java
    Origin:
    	void func()
    	
    midCode:
    	void func()
    	
    Target:
    	#FuncDef func void
    ```

  - ###### 块（Block）的定义

    结构：`#CreateLabel $block-start`

    ​	或者`#CreateLabel $block-final` 

    含义：这里的作用只是在**告诉解释器创建一个新的运行栈**。

    举例：

    ```java
    Origin:
    	int a;
        {
        	int a;
        }
        
    midCode:
    	int a
        $block-start
        int a
        $block-final
        
    Target:
    	#VarDef a int
        #CreateLabel $block-start
        #VarDef a int
        #CreateLabel $block-final	
    ```

    

  - ###### 参数定义

    结构：`#LoadParam` + 变量名

    **只能和函数定义一起出现。**

    举例：

    ```java
    Origin:
    	void func(int a, int b) {}
    	
    midCode:
    	void func()
        para int a
        para int b
        $block-start
        $block-final
        
    Target:
    	#FuncDef func void
        #LoadParam a
        #LoadParam b
        #CreateLabel $block-start
        #CreateLabel $block-final
    ```

    

  - ###### 函数调用和参数传递

    结构：`#Call` + 函数名

    ​			`#Push` + 变量

    ​		由于设计过程中**一定会紧接一个变量记录函数返回值**，故而有一个`#Replace`显示。

    举例：

    ```java
    Origin:
    	func(1);
    	
    midCode:
    	#CALL func
        #PUSH 1
        @Func0 #REPLACE func(1)
    	
    Target:
    	#Call func
    	#Push 1
    	#Replace @Func0 func(1)
    ```

    

  - ###### 函数返回

    结构：`#Return` + 【变量】

    举例：

    ```java
    Origin:
    	return 0;
    	
    midCode:
    	#RETURN 0
    	
    finalCode:
    	#Return 0
    ```

    

  - ###### 创建条件语句（与块同定义）

    结构：`#CreateLabel` + label编号

    举例：见下一个分析

  - ###### 跳转语句

    - 有条件跳转：`#JNR` + Exp + 跳转label

      含义：Exp为0则跳转，否则不跳转

    - 无条件跳转：`#JR` + 跳转label

    举例：

    ```java
    Origin:
    	if (1 > 2) {
            while (1 != 1) {
                break;
            }
        }
    
    Target:
    	#CreateLabel $if-start-0
    	
        #Replace @RelExp0 1 > 2
        #JNR @RelExp0 $if-end-0
        
        #CreateLabel $block-start	//进入if内部
        
        #CreateLabel $while-start-0	//开始while
        
        #Replace @EqExp0 1 != 1
        #JNR @EqExp0 $while-final-0	
        
        #CreateLabel $block-start	//进入while内部
        #JR $while-final-0	//break语句
        #CreateLabel $block-final	//结束while内部
        
        #JR $while-start-0	//while继续循环
        
        #CreateLabel $while-final-0	//结束while
        
        #CreateLabel $block-final	//结束if内部
        
        #JR $if-final-0
        #CreateLabel $if-end-0
        #CreateLabel $if-final-0
    ```

    通过不断设置label，从而能够充分实现作用域的问题，这在之后的解释器中也有体现。

  - ###### 读取语句

    结构：`#Read` + 变量名

    举例：见下

  - ###### 输出语句

    结构：

    - `#WriteStr` + String	输出String
    - `#WriteVar` + 变量      输出变量值

    举例：

    ```java
    Origin:
    	a = getint();
    	printf("A IS %d\n", a);
    
    Target:
    	#Read a
        #WriteStr A IS 
        #WriteVar a
        #WriteStr \n
    ```

    

  - ###### Case语句（补充）

    **case语句的补充主要由于条件语句中一个限制：如果存在“||”和“&&”，前面的判断结果可能会使得后面的判断不进行。**因此需要另外加一个判断，在每次执行一个条件语句后执行一次。
    
    结构：
    
    - `#Case1` + 比较结果 + 最终比较结果：绑定“||”
    
      如果比较结果为1，最终比较结果直接为1，然后跳转
    
    - `#Case0` + 比较结果 + 最终比较结果：绑定“&&”
    
      如果比较结果为0，最终比较结果直接为0，然后跳转
    
    举例：
    
    ```java
    Origin:
    	if(a > 1 || a == 2 && a == 3) 
    	
    Target:
    	#CreateLabel $if-start-0
        #Replace @RelExp0 a > 1
        #Case1 @RelExp0 @LOrExp0	
        	//成功执行后，会跳转到最近的@LOrExp
        #Replace @EqExp0 a == 2
        #Case0 @EqExp0 @LAndExp0
        #Replace @EqExp1 a == 3
        #Case0 @EqExp1 @LAndExp0
        #Replace @LAndExp0 @EqExp0 && @EqExp1
        #Case1 @LAndExp0 @LOrExp0
        #Replace @LOrExp0 @RelExp0 || @LAndExp0
        #JNR @LOrExp0 $if-end-0
    ```
    
    

- ##### 逐一分析

  通过以上对于`PCode`的定义，现在需要解决的是如何实现。而通过之前对于中间代码的定义，该过程相当容易，只需要对每一个语句依次分析即可。

  给出部分的代码解释：

  ```java
  public static void analyse(String mid_code) {
  	if (mid_code.startsWith("#WRITE")) {
  		PrintGenerate(mid_code);
  	} else if (mid_code.startsWith("array int")
  			|| mid_code.startsWith("const ")) {
  		VarDefGenerate(mid_code);
  	} else if (mid_code.startsWith("int ") && 	
  			!mid_code.endsWith(")")) {
  		VarDefGenerate(mid_code);
  	} else if (mid_code.startsWith("int ") && 
  			mid_code.endsWith(")")
  		 || mid_code.startsWith("void ")) {
  		FuncDefGenerate(mid_code);
  	} else {...}
  }
  ```

  而其中每一个调用的函数只需要通过一定的判断（变量、数组、中间变量、常量）生成目标代码即可。

### 		3、解释器

#### （一）文件结构

​		解释器位于`src/PCodeAnalyse/PCodeAnalyser.java`中。

#### （二）实现过程

- ##### 相关类属性的定义

  在解释器中需要定义一些静态类属性来进行程序运行的监控。

  ```java
  //目标代码
  public static ArrayList<String> pcode = PCodeGenerate.codes;
  
  //需要输出的结果
  public static StringBuilder output = new StringBuilder();
  
  //根据要求实例化一次输入
  public static Scanner scanner = new Scanner(System.in);
  
  //监控当前运行的行数（类似于PC值）
  public static int line;
  
  //临时变量，用于传递参数时指明是向哪一个函数传参
  public static PCodeFuncTable funcDef = null;
  
  //当前正在执行的函数块
  public static PCodeFuncBlock funcCall = null;
  
  //call语句使用后，将要调用的函数块（之后赋值给funcCall）
  public static PCodeFuncBlock readyToCall = null;
  ```

  这些定义就能够完成**最重要的函数调用过程**和其他过程。

- ##### 优先处理全局变（常）量和函数定义

  由于程序入口是main函数，因此需要对main函数之前的所有定义进行相关分析，并且记录在符号表格中；**另一方面，由于自身建立了很多`CreateLabel`语句，因此个人编译器同时还会扫描整个程序将所有`#Createlabel`语句记录下来**，从而在进行跳转的时候直接查询label对应的表格即可。

  如下：

  ```java
  public static void run() {
      //记录所有的if和while块，方便跳转时找到跳转地址
      createLabel();
      globalVarAnalyse();
      
      //记录所有的函数以及其起始地址
      globalFuncAnalyse();
      mainAnalyse();
  }
  ```

- ##### 指令单独分析
  
  这里两个较为复杂的指令进行相关分析。
  
  - ###### 函数调用和返回
  
    函数调用的标志是call语句和push语句。在`mainAnalyse()`方法中执行如下：
  
    ```java
    else if (code.startsWith("#Call")) {
    	//该函数涉及到建立新的运行栈
    	CallFuncAnalyse(code);
    	
    	//由于push(传参)一定紧跟在call后面，因此直接在此循环
    	while (pcode.get(line + 1).startsWith("#Push")) {
    		//该函数涉及到一些变量类型和维度的判断
    		PushAnalyse(pcode.get(line + 1));
    		line += 1;
    	}
    	
    	//这里需要记录函数返回地址
    	readyToCall.returnLine = line + 1;
    	
    	//然后切换当前调用函数
    	funcCall = readyToCall;
    	
    	//最后换到此调用函数的起始地址
    	line = funcCall.funcTable.startLine - 1;
    }
    ```
  
    函数返回的标志有两个：
  
    - 如果该函数没有`return`语句，那么遇到下一个`#FuncDef`终止
    - 如果存在`return`语句，只要运行到`return`就直接返回
  
    具体定义如下：
  
    ```java
    if (code.startsWith("#Return") ||code.startsWith("#FuncDef"))
    {
    	ReturnAnalyse(code);//right
    }
    
    public static void ReturnAnalyse(String code) {
    	//存储函数返回值
    	...
    	//删除整个函数调用的运行栈
    	PCodeTableIndex.deleteFuncBlock();
    
    	//这里是main函数的返回，也是整个程序结束的标识
    	if (funcCall.returnLine == 0) {
    		return;
    	}
    	
    	//先回到调用前的PC值，此时所对应的语句一定是记录函数的返回值(#Replace)
    	line = funcCall.returnLine;
    	
    	...
    	
    	//还原到之前调用的函数块
    	funcCall = PCodeTableIndex.funcBlockStack.peek();
    }
    ```
  
    通过`Stack`结构，**每当函数调用时，保留现场一些变量，记录下一条语句地址，开辟一个新的运行栈，从而跳转到调用函数的地址执行；调用结束后，存储函数返回值，删除调用函数的运行栈，然后返回到下一条语句地址，并恢复之前的所有信息。**
  
    以上，就是整个函数调用和函数返回的整体过程，和实际编译器的操作十分相似。
  
    
  
  - ###### 条件语句（block的生成和结束）
  
    ​		实际上条件语句主要就是跳转，因为每次遇到条件语句（或者说程序中的`#CreateLabel`语句），就需要创建block，这一点和函数类似；但是不同的是，每次函数调用是直接建立整个运行栈，不存在函数里面套有一个函数，而条件语句可以循环嵌套。因此，**整个条件语句都是在当前调用函数下进行**。
  
    ​		设计中主要是`LabelAnalyse`函数和`GOTOAnalyse`函数，另外还有新加的`CaseAnalyse`函数。
  
    ​		这里考虑还是不给出代码分析了（相对复杂），对其整个逻辑进行语言描述如下：
  
    - `LabelAnalyse`函数：
  
      - 在遇到label中含有start——证明此时需要在当前函数的顶层block下新建一个block
  
      - 在遇到label中含有final——证明此时需要删除当前顶层block的顶层子block；如果没有子block则删除顶层block
  
      - 在遇到label中含有`end`——视情况而定（if语句）
  
        如果对以上存在疑问可以回看<span style=color:blue>中间代码--实现原理和过程--stmt分析</span>。
  
    - `GotoAnalyse`函数：
  
      实际上这个函数没有特别需要说明的，因为只会遇到跳转指令`#JR`和`#JNR`指令。
  
      - 遇到jr指令，直接跳转到后面label所对应地址即可（查表）
      - 遇到jnr，先判断后面一个变量的值，根据值进行跳转。
  
    - `CaseAnalyse`函数
  
      这个函数和`GotoAnalyse`函数非常类似，只会遇到`Case0`和`Case1`。
  
      - 遇到`Case0`，如果后面一个变量值为0，则后面第二和变量直接为0，然后进行跳转比较
      - 遇到`Case1`，如果后面一个变量值为1，则后面第二个变量直接为1，然后跳转。
  
      上面已经说明，该函数是为了应对条件语句中的`||`和`&&`优先级而设计的。
  
  
  
  这里只说明最重要的这两种，而对于其他指令的实现较为简单，类似于计组过程中的一一指令分析，就不再赘述；而对于如何创建block实现作用域，将在最后的内容——符号表的建立中说明。

### 		4、符号表的建立

​		符号表的文件位置如下：可以看到总共有7个文件；其中主要由`PCodeFuncBlock`和`PCodeBlock`实现作用域，`PCodeTableIndex`是整个表中最重要的索引。

![1639815321103](C:\Users\abb255\AppData\Roaming\Typora\typora-user-images\1639815321103.png)

#### （一）设计

- 变量

  实现变量存储的文件有三个：`PCodeMidCode`、`PCodeParamTable`、`PCodeVarTable`。从我的理解来看，`VarTable`实际上是`ParamTable`的具体实现，因此我将`VarTable`继承于`ParamTable`。其中属性如下：

  ```java
  //对于中间变量，只需要存储名称和值
  public class PCodeMidTable {
      public String name;
      public int value;
  }
  
  //对于参数，需要存储名称和维度
  //对于不同的维度，同时还存在多个维度值
  //对于0维，只有一个value属性
  //对于多维，利用hash表存储value值。
  //有时候参数会当做实际变量进行使用，因此有一个transferVar将参数包装成变量
  //这个函数实际上是因为父类不能强制转化为子类添加的
  public class PCodeParamTable {
      public String name;
      public int lev;
  
      //only accessible for one array definition
      public int loadExp1;
  
      //only accessible for two array definition
      public int loadConstExp2;
  
      //only accessible for single value
      public int value;
  
      //only accessible for array
      public HashMap<Integer, Integer> values = new HashMap<>();
  
      //for transition
      public PCodeVarTable transferVar() {...}
  }    
  
  //一维维度和二维维度，其余父类已定义
  public class PCodeVarTable extends PCodeParamTable{
      public int lev1;
      public int lev2;
  }
  ```

  

- 函数

  对于函数的定义如下：

  ```java
  public class PCodeFuncTable {
      public String name;
      
      //参数组
      public HashMap<String, PCodeParamTable> params = new HashMap<>();
      public ArrayList<String> paramIndex = new ArrayList<>();
      
      //函数起始地址
      public int startLine = 0;
  }
  ```

- 块

  块和block在作用域问题中一起说明。

- 索引

  `PCodeTableIndex`的属性定义如下：

  ```java
  public class PCodeTableIndex {
      //用于函数调用，显示当前已经调用几层（尤其针对交叉调用和递归 ）
      public static Stack<PCodeFuncBlock> funcBlockStack = new Stack<>();
      
      //函数定义表
      public static HashMap<String, PCodeFuncTable> funcTableHashMap = new HashMap<>();
      
      //label定义表（指向label对应的行数）
      public static HashMap<String, Integer> labelQuickFetch = new HashMap<>();
      
      //全局变量表，这里使用block表示，因为全局变量也可以视为一个域
      public static PCodeBlock globalVarTable = new PCodeBlock(null, 0);
  }
  ```

  从上述可以看出，并没有对于中间变量、普通变量等建立索引，因为中间变量和普通变量一定是在函数内部使用的，从而应该将除了全局变量之外的所有变量定义在函数块内部。这一点在作用域分析中也有体现。

#### （二）作用域问题

- Block分析

  针对程序中的block有两类：**函数调用的block和函数内部的block**。

  - 函数调用的block——`PCodeFuncBlock.java`

    函数调用时的block如下：

    ```java
    public class PCodeFuncBlock {
    	//函数内部建立的block栈结构
    	public Stack<PCodeBlock> blockStack = new Stack<>();
        //用于表示此时block的深度，
        public int lev = 1;
        //返回值
        public int returnValue = 0;
        //返回地址
        public int returnLine = 0;
        //调用的是哪一个函数
        public PCodeFuncTable funcTable = null;
    }
    ```

    此外定义了两个方法用于管理函数内部的block：

    ```java
    //每次新建一个block的时候，需要将display区域填充
    //display即该block能够访问变量的区域
    //从而控制了作用域
    public void addNewBlock(String name) {
    	lev += 1;
    	PCodeBlock pCodeBlock = new PCodeBlock(this, this.lev);
    	for (PCodeBlock block : blockStack) {
    		if (block.lev < lev) {
    			pCodeBlock.display.add(block);
    		}
    	}
    	pCodeBlock.name = name;
    	blockStack.push(pCodeBlock);
    }
    
    //删除从整个函数块中移除
    public void deleteBlock() {
    	lev -= 1;
    	blockStack.pop();
    }
    ```

    之前在PCodeTableIndex中记录了当前函数块的栈

    ```
     public static Stack<PCodeFuncBlock> funcBlockStack = new Stack<>();
    ```

    因此每次调用新的函数，需要新建一个函数压入栈顶，函数调用结束后移出栈顶，将当前调用函数指向此时的栈顶即可。

    

  - 函数内部的block——`PCodeBlock`

    在该类中定义属性如下：

    ```java
    public String name;
    //指向当前的函数调用块
    public PCodeFuncBlock funcBlockFather;
    
    //当前block下的新建立的变量
    public HashMap<String, PCodeVarTable> varTables = new HashMap<>();
    public HashMap<String, PCodeMidTable> midTables = new HashMap<>();
    
    //display区域
    public ArrayList<PCodeBlock> display = new ArrayList<>();
    //来源于当前的函数调用块
    public int lev;
    ```

    通过上述的定义，因而可以实现不同作用域中的变量沟通：

    ```java
    public PCodeVarTable getVarTable(String name) {
        //当前作用域找
    	if (varTables.containsKey(name)) {
    		return varTables.get(name);
    	}
        
        //在可以访问的display模块中找
        //其中全局变量由于是PCodeBlock定义的，因此已经存入display.get(0)
    	for (int i = display.size() - 1; i >= 0; i --) {
    		PCodeBlock pCodeBlock = display.get(i);
    		if (pCodeBlock.getVarTable(name) != null) {
    			return pCodeBlock.getVarTable(name);
    		}
    	}
    	return null;
    }
    ```

    实现了变量之间的沟通，从而就实现了作用域真正的功能。

## 六、代码生成编码后改进

### 针对条件语句先后执行的改进

​		之前一直忽略了所谓编译文档中“短路求值”的规则含义；而当我将整个编译过程设计完成，包括作用域的一系列问题都解决了之后突然de出了这一个致命的问题。如何在已有的整体架构上实现这一突然出现的问题确实让我不知所措。吸取了第一次错误处理时的重构教训，因此极力考虑如何能够满足短路求值的判断。

​		最后从中间代码入手，因为我发现自己在设计中间代码的过程中，条件语句的最终中间变量的名称是确定的——一定是**`@LOrExp` + 次数**，因此考虑到新增一个指令定义`Case`，从而解决了这一问题，避免了可能重新设计的风险。

​		可以看到中间代码对于条件语句的生成如下：

```java
//在Load_LAndExp的addSentence方法中

midInterface = new MidTable();
midInterface.name = "@LAndExp" + varNum;
varNum += 1;
MidInterface a;
MidInterface b;
...
midCode.add(midInterface.name + " #REPLACE " + a.name + " && " + b.name);
```

​		因此实际上最终表示条件比较语句结果的中间代码是预先知道的：类名+次数。

​		由于这个信息存在，因此在上述语句比较之前可以增加一条语句：

```
midCode.add("#Case0 " + exp.midInterface.name + " @LAndExp" + varNum);
```

​		从而解释器执行过程中会优先遇到`Case`语句，从而实现短路求值截断，及时作出判断了。