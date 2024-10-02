import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class home_toolbar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置Toolbar等其他初始化代码...  
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 加载菜单资源文件到menu中  
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true; // 表示菜单创建成功  
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理菜单项点击事件  
        switch (item.getItemId()) {
            case R.id.backup:
                showToast("You clicked backup");
                break;
            case R.id.settings:
                showToast("You clicked settings");
                break;
            case R.id.delete:
                showToast("You clicked delete");
                break;
            case R.id.choose_1:
                showToast("You clicked choose_1");
                break;
            case R.id.choose_2:
                showToast("You clicked choose_2");
                break;
            case R.id.other1:
                showToast("You clicked other_1");
                break;
            case R.id.other2:
                showToast("You clicked other_2");
                break;
            default:
                // 处理未知菜单项或不做处理  
                return super.onOptionsItemSelected(item);
        }
        return true; // 表示事件已处理  
    }

    // 辅助方法，用于显示Toast消息  
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}