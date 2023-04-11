# Five-stage-pipeline-simulator

This project is a simulator of a five-stage pipelined processor, implemented in the Java programming language. The simulator supports instructions such as ADD, SUB, MUL, DIV, LOAD, and STORE, where ADD, SUB, LOAD, and STORE require one cycle to execute, and MUL and DIV require two cycles to execute. LOAD and STORE require three cycles to access memory. The IF, ID, and WB stages always execute in one cycle.

Each instruction has a maximum of three register operands, and a sequence of instructions is passed as input. The output displays the stages (or stalls) in which the input instructions (rows) are located for each cycle (column), in a tabular format.

The simulator enables the specification of whether or not data forwarding will be performed between pipeline stages. It also allows the printing of cases where data forwarding occurs, along with all relevant information (instruction numbers and their execution stages during forwarding).

Additionally, the simulator implements detection of hazards (WAR, RAW, and WAW dependencies) between instructions in the input sequence.
