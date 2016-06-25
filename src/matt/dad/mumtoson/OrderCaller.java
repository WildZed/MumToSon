package matt.dad.mumtoson;

import android.media.MediaPlayer;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
// import android.content.Context;
// import android.content.SharedPreferences;
// import android.content.res.Resources;
import android.view.Menu;
import android.view.View;
// import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.util.Log;
import matt.dad.mumtoson.R;
// import matt.dad.mumtoson.NetworkTask;
import zed.tools.lib.nsdconnect.NsdConnection;
import zed.tools.lib.nsdconnect.NsdHelper;


@SuppressLint( "HandlerLeak" )
@TargetApi( Build.VERSION_CODES.JELLY_BEAN )
public class OrderCaller extends Activity
{
    // Constants:
    private static final String ORDER_CALLER = "OrderCaller";
    private static final String ERROR        = "Error";
    private static final String WARNING      = "Warning";
    // @SuppressWarnings( "unused" )
    private static final String INFO         = "Info";


    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_order_caller );

        // Look up some resources from the activity that we need to access.
        m_mainLayout = (LinearLayout) findViewById( R.id.mainLayout );
        // m_commandButtonLayout = (LinearLayout) findViewById( R.id.commandButtons );
        // m_receiveButtonLayout = (LinearLayout) findViewById( R.id.receiveButtons );
        // m_inputMethodManager = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
        // m_resources = getResources();
        // m_ipAddressTxt = (EditText) findViewById( R.id.ip_address );
        // m_portTxt = (EditText) findViewById( R.id.port );
        m_logTxt = (EditText) findViewById( R.id.log_view );
        m_musicPracticeSendBtn = (Button) findViewById( R.id.music_prac );
        m_musicPracticeRcvBtn = (Button) findViewById( R.id.music_prac_rcv );
        m_coffeeSendBtn = (Button) findViewById( R.id.cup_coffee );
        m_coffeeRcvBtn = (Button) findViewById( R.id.cup_coffee_rcv );
        m_homeworkSendBtn = (Button) findViewById( R.id.homework_rev );
        m_homeworkRcvBtn = (Button) findViewById( R.id.homework_rev_rcv );
        m_homeworkSound = MediaPlayer.create( getApplicationContext(), R.raw.homework );
        m_coffeeSound = MediaPlayer.create( getApplicationContext(), R.raw.coffee );
        m_musicPracticeSound = MediaPlayer.create( getApplicationContext(), R.raw.music_practice );

        m_mainLayout.requestFocus();
        // loadPreferences();

        initialiseNsdUpdateHandler();

        // Start the network connectivity.
        initialiseConnection();
        // autoConnect();

        // Button press event listeners. These will send the commands to the listening apps.
        // m_musicPracticeSendBtn.setOnClickListener( m_musicPractiseBtnListener );
        // m_coffeeSendBtn.setOnClickListener( m_coffeeBtnListener );
        // m_homeworkSendBtn.setOnClickListener( m_homeworkBtnListener );
    }


    private void initialiseConnection()
    {
        if ( m_nsdConnection != null || m_nsdHelper != null )
        {
            logMessage( ERROR, "NSD connection already created!" );
            return;
        }

        m_nsdConnection = new NsdConnection( m_nsdUpdateHandler );

        m_nsdHelper = new NsdHelper( this, ORDER_CALLER );
        m_nsdHelper.initializeNsd();
    }


    private void startService()
    {
        if ( m_nsdConnection == null )
        {
            logMessage( ERROR, "No NSD connection!" );
            return;
        }
        
        if ( m_nsdHelper == null )
        {
            logMessage( ERROR, "No NSD helper!" );
            return;
        }
        
//        if ( m_nsdHelper.getChosenServiceInfo() != null )
//        {
//            logMessage( INFO, "NSD helper service already chosen!" );
//            return;      
//        }

        // Register service.
        if ( ! m_nsdHelper.isServiceRegistered() )
        {
            if ( m_nsdConnection.getLocalServerPort() > -1 )
            {
                m_nsdHelper.registerService( m_nsdConnection.getLocalServerPort() );
            }
            else
            {
                logMessage( ERROR, "Server socket isn't bound." );
            }
        }
    }
    
    
    private void connect()
    {
        if ( m_nsdConnection == null || m_nsdHelper == null )
        {
            logMessage( ERROR, "No NSD connection!" );
            return;
        }
        
        // I don't think this is necessary because the NsdConnection object will do this.
        NsdServiceInfo service = m_nsdHelper.getChosenServiceInfo();

        if ( service != null )
        {
            logMessage( INFO, "Connecting..." );
            m_nsdConnection.connectToServer( service.getHost(), service.getPort() );
        }
        else
        {
            logMessage( INFO, "No service to connect to!" );
        }
    }


    // private void loadPreferences()
    // {
    // SharedPreferences settings = getPreferences( Context.MODE_PRIVATE );
    // String ipAddress = settings.getString( "ip", m_resources.getText( R.string.ip_default ).toString() );
    // String port = settings.getString( "port", m_resources.getText( R.string.port_default ).toString() );
    // String sendTxt = settings.getString( "sendText", "" );
    //
    // m_ipAddressTxt.setText( ipAddress );
    // m_portTxt.setText( port );
    // m_sendTxt.setText( sendTxt );
    // }

    // private void savePreferences()
    // {
    // SharedPreferences settings = getPreferences( Context.MODE_PRIVATE );
    // SharedPreferences.Editor editor = settings.edit();
    //
    // editor.putString( "ip", m_ipAddressTxt.getText().toString() );
    // editor.putString( "port", m_portTxt.getText().toString() );
    // editor.putString( "sendText", m_sendTxt.getText().toString() );
    //
    // // Commit the edits!
    // editor.commit();
    // }

    // private void loseKeyboard()
    // {
    // m_inputMethodManager.hideSoftInputFromWindow( m_mainLayout.getWindowToken(), 0 );
    // }

    // private void connect()
    // {
    // if ( null != m_networkTask )
    // {
    // m_networkTask.cancel( true );
    // m_networkTask = null;
    // }
    //
    // logLabel( "Connecting...", false );
    //
    // String ipStr = m_ipAddressTxt.getText().toString();
    // int port = Integer.parseInt( m_portTxt.getText().toString() );
    //
    // m_networkTask = new NetworkTask( ipStr, port, this );
    // m_networkTask.execute();
    // }

    // private void setConnected( boolean isConnected )
    // {
    // if ( isConnected )
    // {
    // // m_connectBtn.setEnabled( false );
    // m_sendBtn.setEnabled( true );
    // logLabel( "Connected.", true );
    // }
    // else
    // {
    // // m_connectBtn.setEnabled( true );
    // m_sendBtn.setEnabled( false );
    // logLabel( "Disconnected.", true );
    //
    // if ( null != m_networkTask )
    // {
    // m_networkTask.cancel( true );
    // m_networkTask = null;
    // }
    // }
    // }

    // private void sendMessage()
    // {
    // String sendStr = m_sendTxt.getText().toString();
    // int selStart = m_sendTxt.getSelectionStart();
    // int selEnd = m_sendTxt.getSelectionEnd();
    //
    // if ( selStart != selEnd )
    // {
    // sendStr = sendStr.substring( selStart, selEnd );
    // }
    //
    // logMessage( "Sending message:", sendStr );
    //
    // m_networkTask.sendDataToNetwork( sendStr );
    // }

    public void clickMusicPractice( View view )
    {
        m_musicPracticeRcvBtn.setEnabled( true );
        sendMessageToNSDConnection( "m+" );
    }


    public void clickCoffee( View view )
    {
        m_coffeeRcvBtn.setEnabled( true );
        sendMessageToNSDConnection( "c+" );
    }


    public void clickHomework( View view )
    {
        m_homeworkRcvBtn.setEnabled( true );
        sendMessageToNSDConnection( "h+" );
    }

    
    public void clickMusicPracticeRcv( View view )
    {
        m_musicPracticeRcvBtn.setEnabled( false );
        sendMessageToNSDConnection( "m-" );
    }


    public void clickCoffeeRcv( View view )
    {
        m_coffeeRcvBtn.setEnabled( false );
        sendMessageToNSDConnection( "c-" );
    }


    public void clickHomeworkRcv( View view )
    {
        m_homeworkRcvBtn.setEnabled( false );
        sendMessageToNSDConnection( "h-" );
    }

    
    public void clickLogView( View view )
    {
        connect();
    }


    private void sendMessageToNSDConnection( String msg )
    {
        logMessage( INFO, "Sending message '" + msg + "' to NSD connection..." );

        if ( m_nsdConnection != null )
        {
            m_nsdConnection.sendMessage( msg );
        }
    }


    private void setMumButtonsEnabled( boolean enabled )
    {
        logMessage( INFO, "Mum buttons set to " + enabled + "." );

        m_musicPracticeSendBtn.setEnabled( enabled );
        m_coffeeSendBtn.setEnabled( enabled );
        m_homeworkSendBtn.setEnabled( enabled );
    }


    protected void processMumCommand( String msg )
    {
        // We've received a message, let's see what it is.
        // For some reason the msg is "them: <char>".
        boolean enabled = ( msg.charAt( 1 ) == '+' );
        
        switch ( msg.charAt( 0 ) )
        {
            case 'm':
                m_musicPracticeRcvBtn.setEnabled( enabled );
                
                if ( enabled )
                {
                    m_musicPracticeSound.start();
                }
                break;

            case 'c':
                m_coffeeRcvBtn.setEnabled( enabled );
                
                if ( enabled )
                {
                    m_coffeeSound.start();
                }
                break;

            case 'h':
                m_homeworkRcvBtn.setEnabled( enabled );
                
                if ( enabled )
                {
                    m_homeworkSound.start();
                }
                break;

            default:
                logMessage( WARNING, "Unhandled Mum command!" );
                break;
        }
    }


    private void logMessage( String label, String message )
    {
        String logMsg = label + ": " + message;

        m_logTxt.append( logMsg );

        if ( ! logMsg.endsWith( "\n" ) )
        {
            m_logTxt.append( "\n" );
        }

        Log.d( ORDER_CALLER, logMsg );
    }


    // private View.OnClickListener m_musicPractiseBtnListener = new View.OnClickListener()
    // {
    // public void onClick( View v )
    // {
    // // loseKeyboard();
    // // connect();
    // sendMumCommand( 'm' );
    // }
    // };
    //
    // private View.OnClickListener m_coffeeBtnListener = new View.OnClickListener()
    // {
    // public void onClick( View v )
    // {
    // // loseKeyboard();
    // sendMumCommand( 'c' );
    // }
    // };
    //
    // private View.OnClickListener m_homeworkBtnListener = new View.OnClickListener()
    // {
    // public void onClick( View v )
    // {
    // // loseKeyboard();
    // sendMumCommand( 'h' );
    // }
    // };

    private void initialiseNsdUpdateHandler()
    {
        m_nsdUpdateHandler = new Handler()
        {
            @Override
            public void handleMessage( Message nsdMsg )
            {
                Bundle data = nsdMsg.getData();
                String msg = data.getString( "msg" );

                if ( msg != null )
                {
                    if ( msg.startsWith( "me: " ) )
                    {
                        String meMsg = msg.substring( 4 );
                        
                        logMessage( INFO, "Sent message to remote: " + meMsg );
                    }
                    else if ( msg.startsWith( "them: " ) )
                    {
                        String themMsg = msg.substring( 6 );
                        
                        logMessage( INFO, "Received message from remote: " + themMsg );
                        
                        processMumCommand( themMsg );
                    }
                    else
                    {
                        logMessage( WARNING, "Unrecognised message: " + msg );
                    }
                }

                String connected = data.getString( "connected" );

                if ( connected != null )
                {
                    boolean enabled = ( connected == "1" );

                    setMumButtonsEnabled( enabled );
                }
            }
        };
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        // Start local service if not registered.
        startService();
   }


    @Override
    protected void onRestart()
    {
        super.onRestart();
        // Start local service if not registered.
        // onStart() always gets called after onRestart().
        // startService();
    }


    @Override
    protected void onStop()
    {
        super.onStop();
        // setConnected( false );
        // savePreferences();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        
        if ( m_nsdHelper != null )
        {
            m_nsdHelper.discoverServices();
        }
    }


    @Override
    protected void onPause()
    {
        if ( m_nsdHelper != null )
        {
            m_nsdHelper.stopDiscovery();
        }

        super.onPause();
    }


    @Override
    protected void onDestroy()
    {
        try
        {
            m_nsdHelper.tearDown();
            m_nsdConnection.tearDown();
        }
        catch ( RuntimeException e )
        {
            Log.e( ORDER_CALLER, "Run time exception: ", e );
            e.printStackTrace();
        }
        catch ( Exception e )
        {
            Log.e( ORDER_CALLER, "Unknown exception: ", e );
            e.printStackTrace();     
        }
        
        super.onDestroy();
        // setConnected( false );
    }


    @Override
    protected void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState( outState );
    }


    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        super.onCreateOptionsMenu( menu );

        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate( R.menu.music_prac, menu );

        return true;
    }


    public void onConnect()
    {
        // setConnected( true );
    }


    public void onReceiveInput( String data )
    {
        logMessage( "Received message:", data );
    }


    public void onDisconnect()
    {
        // setConnected( false );
    }


    private NsdHelper     m_nsdHelper;
    private NsdConnection m_nsdConnection;

    private LinearLayout  m_mainLayout;
    // private LinearLayout m_commandButtonLayout;
    // private LinearLayout m_receiveButtonLayout;
    // private InputMethodManager m_inputMethodManager;
    // private Resources m_resources;
    // private EditText m_ipAddressTxt;
    // private EditText m_portTxt;
    // private EditText m_sendTxt;
    private EditText      m_logTxt;
    private Button        m_musicPracticeSendBtn;
    private Button        m_musicPracticeRcvBtn;
    private Button        m_coffeeSendBtn;
    private Button        m_coffeeRcvBtn;
    private Button        m_homeworkSendBtn;
    private Button        m_homeworkRcvBtn;
    // private NetworkTask m_networkTask;
    private Handler       m_nsdUpdateHandler;
    private MediaPlayer   m_homeworkSound;
    private MediaPlayer   m_coffeeSound;
    private MediaPlayer   m_musicPracticeSound;
}
