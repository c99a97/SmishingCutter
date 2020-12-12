<?php
	require_once "sc_header.php";
	require_once "sc_DB_conn.php";
	require_once "sc_menubar.php";
?>
<?php
	$_GET['page_no'] = mysqli_real_escape_string($db_conn, $_GET['page_no']);
	$_GET['list_num'] = mysqli_real_escape_string($db_conn, $_GET['list_num']);
	$_GET['method'] = mysqli_real_escape_string($db_conn, $_GET['method']);
	$_GET['search'] = mysqli_real_escape_string($db_conn, $_GET['search']);
	// 검색어 설정
	function addSearch($pageNo){
		global $howMany;
		global $searchHow, $searchWhat;
		$resultStr = "sc_index.php?page_no=".$pageNo;

		if($howMany!=NULL && $howMany!=20){
			$resultStr = $resultStr."&list_num=".$howMany;
		}
		if($searchHow!=NULL && $searchWhat!=NULL){
			$resultStr = $resultStr."&method=".$searchHow."&search=".$searchWhat;
		}
		return $resultStr;
	}

	// 페이지번호 설정
	if(empty($_GET['page_no'])){
		$pageNo = 1;
	}else{
		$pageNo = $_GET['page_no'];
	}
	// 출력개수 설정
	if(empty($_GET['list_num'])){
		$howMany = 20;
	}else{
		$howMany = $_GET['list_num'];
		if($howMany!=20 && $howMany!=30 && $howMany!=50 && $howMany!=100){
	        $howMany = 20;
	    }
	}
	$URLSetting = "sc_index.php";
	// 검색방법 설정
    if(isset($_GET['method'])&&isset($_GET['search'])){
        $searchHow = $_GET['method'];
        $searchWhat = $_GET['search'];

        switch($searchHow){
            case how_URL:
                $searchStr = "where URL like \"%".$searchWhat."%\"";
                break;
            case how_no:
                $searchStr = "where URL_no=".$searchWhat;
                break;
            case how_sender:
                $searchStr = "where sender_number like \"%".$searchWhat."%\"";
                break;
            case how_receiver:
                $searchStr = "where receiver_number like \"%".$searchWhat."%\"";
                break;
            case how_class:
                if($searchWhat=='차단')
                    $searchWhat=0;
                else if($searchWhat=='허가')
                    $searchWhat=1;
                else if($searchWhat=='안전')
                    $searchWhat=2;
                else{
                    $searchHow = NULL;
                    $searchWhat = NULL;
                    break;
                }
                $searchStr = "where is_block=".$searchWhat;
                break;
            default:
                $searchHow = NULL;
                $searchWhat = NULL;
        }
    }else{
        $searchHow = NULL;
        $searchWhat = NULL;
    }
?>
        <form name='line_num' class='formNoLine' method='GET' action='sc_index.php'>
            출력물 개수 :
            <select onchange="this.form.submit()" id='list_num' name='list_num'>
                <option value='20'>20개</option>
                <option value='30' <?php if($howMany==30) echo "selected"; ?>>30개</option>
                <option value='50' <?php if($howMany==50) echo "selected"; ?>>50개</option>
                <option value='100' <?php if($howMany==100) echo "selected"; ?>>100개</option>
            </select>
			<input type='hidden' name='method' value='<?php echo $searchHow; ?>'>
			<input type='hidden' name='search' value='<?php echo $searchWhat; ?>'>
        </form></td>
	</tr>
    <tr height='25px'>
        <th class='thGrayC' width='100' style='letter-spacing: 4px;'>순번</th>
        <th class='thGrayC' width='75' style='letter-spacing: 4px;'>분류</th>
        <th class='thGrayC' width='650' style='letter-spacing: 8px;'>URL</th>
        <th class='thGrayC' width='250' style='letter-spacing: 4px;'>일자</th>
        <th class='thGrayC' width='175' style='letter-spacing: 3px;'>발신번호</th>
        <th class='thGrayC' width='175' style='letter-spacing: 3px;'>수신번호</th>
        <th class='thGrayC' width='100' style='letter-spacing: 3px;'>신고누적</th>
    </tr>
<?php
    // 튜플 출력
	$db_res = mysqli_query($db_conn,"select count(*) from SC_URL $searchStr");
    $db_row = mysqli_fetch_row($db_res);
    $row_num = $db_row[0];
	// 페이지 당 출력물 범위 계산
    $calFrom = ($pageNo-1)*$howMany;
    if($calFrom+$howMany > $row_num){
        $calTo = ($row_num%$howMany);
    }else{
        $calTo = $howMany;
    }
	// 출력할 게시물이 있는지
    if($row_num == 0){
		echo "<tr>";
		for($i=0; $i<7; $i++)
			echo "<td class='tdCenterU'>-</td>";
		echo "</tr>\n";
    }else{
        $db_res = mysqli_query($db_conn,"select URL_no, is_block, URL, modification_time, sender_number, receiver_number, report_num, file_name from SC_URL $searchStr order by URL_no DESC LIMIT $calFrom, $calTo");
        while($db_row = mysqli_fetch_row($db_res)){
		    echo "<tr>";
    		for($i=0; $i<7; $i++){
				if($i==1){
					echo "<td class='tdCenterU' style='padding:5px 0px;'>";
                    if($db_row[1]==0) echo "<p style='color:#EE0022;'>차단</p>";
                    else if($db_row[1]==1) echo "<p style='color:#40C355;'>허가</p>";
                    else if($db_row[1]==2) echo "<p style='color:#4E9FD8;'>안전</p>";
                } else if($i==2){
                    echo "<td class='tdCenterU' width='650px' style='overflow:hidden;'><a href='./sc_URL_view.php?URL_no=".$db_row[0]."'>".$db_row[2]."</a>";
                } else{
					echo "<td class='tdCenterU'>".$db_row[$i];
				}
                echo "</td>";
    		}
    		echo "</tr>\n";
        }
	}
?>
	<tr height='75px'>
		<td class='tdCenter' colspan='7'>
        <form name='search_form' class='formNoLine' method='GET' action='sc_index.php'>
            <select name='method' size='0'>
                <option value='how_URL'>URL</option>
                <option value='how_no' <?php if($searchHow=='how_no') echo "selected"; ?>>순번</option>
                <option value='how_class' <?php if($searchHow=='how_class') echo "selected"; ?>>분류</option>
                <option value='how_sender' <?php if($searchHow=='how_sender') echo "selected"; ?>>발신번호</option>
                <option value='how_receiver' <?php if($searchHow=='how_receiver') echo "selected"; ?>>수신번호</option>
            </select>
            <input type='text' name='search' style='width: 500px;' <?php echo "value=".$searchWhat; ?>>
            <input type='submit' class='butWhiteH' style='letter-spacing: 4px;' value='검색'>
			<button type='button' class='butWhiteH' style='letter-spacing: 3px;' onclick="location.href='sc_URL_add.php'">추가</button>
        </form>
    	</td>
		<!--<td class='tdCenter' colspan='2'></td>-->
	</tr>
    <tr>
		<td class='tdCenter' colspan='7'>
<?php
	// 아래 페이지번호 출력 부분
    $pageMax = ceil($row_num/$howMany);
    $pgMax = floor(($pageMax-1)/10);
    $pgNow = floor(($pageNo-1)/10);
    if($pgMax < 0) $pgMax = 0;
    if($pgNow < 0) $pgNow = 0;
    echo "<button type='button' class='butWhiteH' onclick=\"location.href='".addSearch(1)."'\"".(($pgNow!=0)?"":" disabled").">처음</button> ";
	echo "<button type='button' class='butWhiteH' onclick=\"location.href='".addSearch($pgNow*10-9)."'\"".(($pgNow!=0)?"":" disabled").">이전</button> ";
	for($i=$pgNow*10+1; ($i<=$pgNow*10+10) && ($i<=$pageMax); $i++)
		echo "<u><a href=".addSearch($i).">".$i."</a></u> ";
	echo "<button type='button' class='butWhiteH' onclick=\"location.href='".addSearch($pgNow*10+11)."'\"".(($pgNow!=$pgMax)?"":" disabled").">다음</button> ";
	echo "<button type='button' class='butWhiteH' onclick=\"location.href='".addSearch($pageMax)."'\"".(($pgNow!=$pgMax)?"":" disabled").">끝</button>";
?>
		</td>
	</tr>
	</table>
<?php
	mysqli_close($db_conn);
	require_once "sc_footer.php";
?>
