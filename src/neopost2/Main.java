package neopost2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {

  private static Label infoLabel;
  private static Table table;
  private static String queryTime;
  private static Properties props;

  public static void main(String[] args) throws IOException {
    props = new Properties();
    File propFile = new File(Country.getStoredDir(), "dialog");
    if (propFile.isFile()) {
      props.loadFromXML(new FileInputStream(propFile));
    }
    ApartmentRegistry.getInstance().loadFilters(props);

    Display display = new Display();
    Shell shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
    shell.setSize(1920, 1080);
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
        queryTime = null;
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
    gl.numColumns = 12;
    filterPane.setLayout(gl);

    Text interestText = new Text(filterPane, SWT.BORDER | SWT.FLAT | SWT.RIGHT);
    interestText.setText("" + ApartmentRegistry.getInstance().getInterest());
    interestText.addModifyListener(event->{
      try {
        ApartmentRegistry.getInstance().setInterest(Float.parseFloat(interestText.getText()));
        refreshTable();
      } catch (NumberFormatException ignored) {
      }
    });
    gd = new GridData();
    gd.widthHint = 30;
    interestText.setLayoutData(gd);

    Label l = new Label(filterPane, SWT.NONE);
    l.setText("이율");

    Text minDealCountText = new Text(filterPane, SWT.BORDER | SWT.FLAT | SWT.RIGHT);
    minDealCountText.setText("" + ApartmentRegistry.getInstance().getMinTradeCount());
    minDealCountText.addModifyListener(event->{
      try {
        ApartmentRegistry.getInstance().setMinTradeCount(Integer.parseInt(minDealCountText.getText()), true);
        refreshTable();
      } catch (NumberFormatException ignored) {
      }
    });
    gd = new GridData();
    gd.widthHint = 30;
    minDealCountText.setLayoutData(gd);

    l = new Label(filterPane, SWT.NONE);
    l.setText("매매건수 이상");

    Text minTrimmedPriceText = new Text(filterPane, SWT.BORDER | SWT.FLAT | SWT.RIGHT);
    minTrimmedPriceText.setText("" + ApartmentRegistry.getInstance().getTrimmedMinPrice() / 10000);
    minTrimmedPriceText.addModifyListener(event->{
      try {
        ApartmentRegistry.getInstance().setTrimmedMinPrice(Float.parseFloat(minTrimmedPriceText.getText()) * 10000, true);
        refreshTable();
      } catch (NumberFormatException ignored) {
      }
    });
    gd = new GridData();
    gd.widthHint = 30;
    minTrimmedPriceText.setLayoutData(gd);

    l = new Label(filterPane, SWT.NONE);
    l.setText("매매가(80%) 이상");

    Text minPyongText = new Text(filterPane, SWT.BORDER | SWT.FLAT | SWT.RIGHT);
    minPyongText.setText("" + ApartmentRegistry.getInstance().getMinPyong());
    minPyongText.addModifyListener(event->{
      try {
        ApartmentRegistry.getInstance().setMinPyong(Integer.parseInt(minPyongText.getText()), true);
        refreshTable();
      } catch (NumberFormatException ignored) {
      }
    });
    gd = new GridData();
    gd.widthHint = 30;
    minPyongText.setLayoutData(gd);

    l = new Label(filterPane, SWT.NONE);
    l.setText("전용면적 이상");

    Text rentDealCountText = new Text(filterPane, SWT.BORDER | SWT.FLAT | SWT.RIGHT);
    rentDealCountText.setText("" + ApartmentRegistry.getInstance().getMinRentDealCount());
    rentDealCountText.addModifyListener(event->{
      try {
        ApartmentRegistry.getInstance().setMinRentDealCount(Integer.parseInt(rentDealCountText.getText()), true);
        refreshTable();
      } catch (NumberFormatException ignored) {
      }
    });
    gd = new GridData();
    gd.widthHint = 30;
    rentDealCountText.setLayoutData(gd);

    l = new Label(filterPane, SWT.NONE);
    l.setText("임대건수 이상");

    Text rentRatioText = new Text(filterPane, SWT.BORDER | SWT.FLAT | SWT.RIGHT);
    rentRatioText.setText("" + ApartmentRegistry.getInstance().getMinRentRatio() * 100.f);
    rentRatioText.addModifyListener(event->{
      try {
        ApartmentRegistry.getInstance().setMinRentRatio(Integer.parseInt(rentRatioText.getText()) / 100.f, true);
        refreshTable();
      } catch (NumberFormatException ignored) {
      }
    });
    gd = new GridData();
    gd.widthHint = 30;
    rentRatioText.setLayoutData(gd);

    l = new Label(filterPane, SWT.NONE);
    l.setText("80%연임대/보증금 이상");

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
    column.setText("평균매매가");
    column.setWidth(80);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("80%매매가");
    column.setWidth(80);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("매매건수");
    column.setWidth(60);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("평균임대보증금");
    column.setWidth(100);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("80%임대보증금");
    column.setWidth(100);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("평균월임대료");
    column.setWidth(90);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("80%월임대료");
    column.setWidth(90);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("임대건수");
    column.setWidth(60);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("평균연임대/보증금");
    column.setWidth(115);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("80%연임대/보증금");
    column.setWidth(115);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("평균환산보증금/매매가");
    column.setWidth(140);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("80%환산보증금/매매가");
    column.setWidth(140);

    DecimalFormat ukFormat = new DecimalFormat("#.##");
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
      item.setText(5, ukFormat.format(apartment.getAverageTradePrice() / 10000.f) + "억");
      item.setText(6, ukFormat.format(apartment.getTrimmedTradePrice() / 10000.f) + "억");
      item.setText(7, apartment.getTradeCount() + "건");
      float[] rents = apartment.getRentFee();
      float[] trimmedRents = apartment.getTrimmedRentFee();
      item.setText(8, ukFormat.format(rents[0] / 10000.f) + "억");
      item.setText(9, ukFormat.format(trimmedRents[0] / 10000.f) + "억");
      item.setText(10, (int)rents[1] + "만");
      item.setText(11, (int)trimmedRents[1] + "만");
      item.setText(12, apartment.getRentCount() + "건");
      item.setText(13, ukFormat.format(rents[2] * 100) + "%");
      item.setText(14, ukFormat.format(trimmedRents[2] * 100) + "%");
      item.setText(15, ukFormat.format(rents[3] * 100) + "%");
      item.setText(16, ukFormat.format(trimmedRents[3] * 100) + "%");
    });

    queryTime = null;
    refreshTable();
    query(true);

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();

    ApartmentRegistry.getInstance().storeFilters(props);
    props.storeToXML(new FileOutputStream(propFile), "");
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
      ApartmentRegistry.getInstance().applyFilters();
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
    Main.queryTime = queryTime[0];
    refreshTable();
  }

   private static void refreshTable() {
     table.clearAll();
     int count = ApartmentRegistry.getInstance().getFiltered().size();
     table.setItemCount(count);

     int totalCount = ApartmentRegistry.getInstance().getApartments().size();
     int filteredCount = ApartmentRegistry.getInstance().getFiltered().size();
     int thisYear = Calendar.getInstance().get(Calendar.YEAR);
     int thisMonth = Calendar.getInstance().get(Calendar.MONTH);
     String info;
     if (queryTime == null) {
       info = (thisYear - 1) + "/" + (thisMonth - 1) + " ~ " + thisYear + "/" + thisMonth + " [" + filteredCount + "/" + totalCount +"]건 ";
     } else {
       info = (thisYear - 1) + "/" + (thisMonth - 1) + " ~ " + thisYear + "/" + thisMonth + " [" + filteredCount + "/" + totalCount +"]건 [" + queryTime + "]";
     }
     infoLabel.setText(info);
  }
}
