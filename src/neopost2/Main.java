package neopost2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {

  private static Display display;
  private static Shell shell;
  private static Table table;
  private static Label infoLabel;

  public static void main(String[] args) throws IOException {
    display = new Display();
    shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
    shell.setSize(1280, 720);
    shell.setLayout(new GridLayout());
    shell.setText("NeoPost2");

    Composite headerPane = new Composite(shell, SWT.NONE);
    GridData gd = new GridData();
    gd.heightHint = 80;
    gd.horizontalAlignment = SWT.FILL;
    headerPane.setLayoutData(gd);
    GridLayout gl = new GridLayout();
    gl.numColumns = 2;
    headerPane.setLayout(gl);

    infoLabel =  new Label(headerPane, SWT.NONE);
    gd = new GridData();
    gd.horizontalAlignment = SWT.FILL;
    infoLabel.setLayoutData(gd);

    Button qBtn = new Button(headerPane, SWT.PUSH);
    qBtn.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        ApartmentRegistry.getInstance().clearApartments();
        refreshTable();
        setQueryTime(null);
        table.update();
        query(false);
      }
    });
    qBtn.setText("조회");
    gd = new GridData();
    gd.verticalAlignment = SWT.FILL;
    gd.grabExcessVerticalSpace = true;
    gd.horizontalAlignment = SWT.END;
    gd.verticalSpan = 2;
    gd.widthHint = 60;
    qBtn.setLayoutData(gd);

    Group filterPane = new Group(headerPane, SWT.NONE);
    filterPane.setText(" 필터 ");
    gd = new GridData();
    gd.verticalAlignment = SWT.FILL;
    gd.grabExcessHorizontalSpace = true;
    gd.grabExcessVerticalSpace = true;
    gd.horizontalAlignment = SWT.FILL;
    filterPane.setLayoutData(gd);
    gl = new GridLayout();
    gl.numColumns = 2;
    filterPane.setLayout(gl);

    Text minDealCountText = new Text(filterPane, SWT.BORDER | SWT.FLAT | SWT.RIGHT);
    minDealCountText.setText("" + ApartmentRegistry.getInstance().getMinDealCount());
    minDealCountText.addModifyListener(event->{
      try {
        ApartmentRegistry.getInstance().setMinDealCount(Integer.parseInt(minDealCountText.getText()), true);
        refreshTable();
      } catch (NumberFormatException ignored) {
      }
    });
    gd = new GridData();
    gd.widthHint = 30;
    minDealCountText.setLayoutData(gd);

    Label l = new Label(filterPane, SWT.NONE);
    l.setText("매매건수 이상");

    table = new Table(shell, SWT.VIRTUAL | SWT.FULL_SELECTION);
    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    table.setHeaderVisible(true);

    TableColumn column = new TableColumn(table, SWT.LEFT);
    column.setText("시/도");
    column.setWidth(80);
    column = new TableColumn(table, SWT.LEFT);
    column.setText("구/군");
    column.setWidth(120);
    column = new TableColumn(table, SWT.LEFT);
    column.setText("동");
    column.setWidth(75);
    column = new TableColumn(table, SWT.LEFT);
    column.setText("단지");
    column.setWidth(180);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("전용면적");
    column.setWidth(60);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("평균가격");
    column.setWidth(80);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("매매건수");
    column.setWidth(60);

    DecimalFormat ukFormat = new DecimalFormat(".##");
    table.addListener(SWT.SetData, event -> {
      TableItem item = (TableItem)event.item;
      int index = table.indexOf(item);
      List<Apartment> fs = ApartmentRegistry.getInstance().getFiltered();
      if (index >= fs.size()) {
        return;
      }
      Apartment apartment = fs.get(index);
      Danji danji = apartment.getDanji();
      Dong dong = danji.getDong();
      Gugun gugun = dong.getGugun();
      Sido sido = gugun.getSido();
      item.setText(0, sido.getName());
      item.setText(1, gugun.getName());
      item.setText(2, dong.getName());
      item.setText(3, danji.getName());
      item.setText(4, apartment.getPyong() + "평");
      item.setText(5, ukFormat.format(apartment.getAveragePrice() / 10000.f) + "억");
      item.setText(6, apartment.getDealCount() + "");
    });

    refreshTable();
    setQueryTime(null);
    query(true);

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }

  private static void query(boolean useStored) {
    ZipFile zip = null;
    Country country = null;
    String [] queryTime = new String[1];
    try {
      if (useStored) {
        File f = new File(Country.getStoredDir(), "NeoPost2.zip");
        if (!f.isFile()) {
          return;
        }
        zip = new ZipFile(f, ZipFile.OPEN_READ);
        ZipEntry qte = zip.getEntry("qt");
        if (qte != null) {
          Reader reader = new InputStreamReader(zip.getInputStream(qte));
          StringBuilder buffer = new StringBuilder();
          char [] cb = new char [1024];
          int len;
          while ((len = reader.read(cb, 0, cb.length)) != -1) {
            buffer.append(cb, 0, len);
          }
          queryTime[0] = buffer.toString();
        }
      }
      country = new Country(zip);
      country.setTargetFilters(new String[]{"서울특별시", "경기도"});

      long startTime = System.nanoTime();

      ExecutorService es = Executors.newFixedThreadPool(64);
      CompletionService<String> ecs = new ExecutorCompletionService<>(es);
      AtomicInteger count = new AtomicInteger(1);

      final Country c = country;
      ecs.submit(() -> {
        c.populate();
        for (Sido sido : c.getSidos()) {
          count.incrementAndGet();
          ecs.submit(() -> {
            sido.populate();
            for (Gugun gg : sido.getGuguns()) {
              count.incrementAndGet();
              ecs.submit(() -> {
                gg.populate();
                for (Dong dg : gg.getDongs()) {
                  count.incrementAndGet();
                  ecs.submit(() -> {
                    dg.populate();
                    for (Danji dj : dg.getDanjis()) {
                      count.incrementAndGet();
                      ecs.submit(() -> {
                        dj.populate();
                        return dj.getName();
                      });
                    }
                    return dg.getName();
                  });
                }
                return gg.getName();
              });
            }
            return sido.getName();
          });
        }
        return "Country";
      });
      int i = 0;
      while (count.decrementAndGet() >= 0) {
        try {
          Future<String> f = ecs.take();
          System.out.println((++i) + ". " + f.get() + " parsed.");
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
      }
      es.shutdown();
      ApartmentRegistry.getInstance().sortApartments();
      long elapsed = System.nanoTime() - startTime;
      System.out.println(elapsed / 1000000 / 1000.f + " sec. elapsed.");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (zip !=  null) {
        try {
          zip.close();
        } catch (IOException e) {
        }
      }
      if (country != null) {
        String zipTime = Country.closeZipWrite(new File(Country.getStoredDir(), "NeoPost2.zip"));
        if (zipTime != null) {
          queryTime[0] = zipTime;
        }
      }
    }
    refreshTable();
    setQueryTime(queryTime[0]);
  }

   private static void refreshTable() {
     table.clearAll();
     int count = ApartmentRegistry.getInstance().getFiltered().size();
     table.setItemCount(count);
  }

  private static void setQueryTime(String queryTime) {
    int count = ApartmentRegistry.getInstance().getApartments().size();
    int thisYear = Calendar.getInstance().get(Calendar.YEAR);
    int thisMonth = Calendar.getInstance().get(Calendar.MONTH);
    String info;
    if (queryTime == null) {
      info = (thisYear - 1) + "/" + (thisMonth - 1) + " ~ " + thisYear + "/" + thisMonth + " [" + count +"]건 ";
    } else {
      info = (thisYear - 1) + "/" + (thisMonth - 1) + " ~ " + thisYear + "/" + thisMonth + " [" + count +"]건 [" + queryTime + "]";
    }
    infoLabel.setText(info);
  }
}
